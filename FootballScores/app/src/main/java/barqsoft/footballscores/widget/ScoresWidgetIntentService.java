package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.FavoriteTeamDialogFragment;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

/**
 * Created by ibalashov on 2/29/2016.
 */
public class ScoresWidgetIntentService extends IntentService {

    private static final String[] SCORES_COLUMNS = {
            DatabaseContract.scores_table.DATE_COL,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.MATCH_DAY
    };

    public ScoresWidgetIntentService() {
        super("ScoresWidgetIntentService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, startId, startId);
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                FootballAppWidgetProvider.class));
        Date fragmentdate = new Date(System.currentTimeMillis());
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        String date = mformat.format(fragmentdate);
        Uri uri = DatabaseContract.scores_table.buildScoreWithDate();
       Cursor data = getContentResolver().query(uri, SCORES_COLUMNS,
                DatabaseContract.scores_table.DATE_COL + " like ?",new String[] {
                        date + "%"
                },
                DatabaseContract.scores_table.DATE_COL + " desc");
        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String favoriteTeam = sharedPref.getString(FavoriteTeamDialogFragment.PREF_NAME, "");
        if (favoriteTeam.equals("")) {
            favoriteTeam = "Manchester United FC";
        }
        String homeTeam = "";
        String awayTeam = "";
        String score  = "";
        while (data.moveToNext()) {
            String tempHomeTeam = data.getString(1);
            String tempAwayTeam = data.getString(2);
            String homeGoals = data.getString(3);
            String awayGoals = data.getString(4);
            if (tempAwayTeam.equals(favoriteTeam) || tempHomeTeam.equals(favoriteTeam)) {
                homeTeam = tempHomeTeam;
                awayTeam = tempAwayTeam;
                if (homeGoals.equals("-1")) score = getString(R.string.game_not_started_yet);

                else score = homeGoals + " : " + awayGoals;
                break;
            }
        }

        data.close();



        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.football_appwidget;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            if (homeTeam.equals("") || awayTeam.equals("")) {
                views.setTextViewText(R.id.teams, "No games today");
            }
            else {
                views.setTextViewText(R.id.teams, homeTeam + " - " + awayTeam);
            }
            views.setTextViewText(R.id.results_text, score);
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }



    }
}
