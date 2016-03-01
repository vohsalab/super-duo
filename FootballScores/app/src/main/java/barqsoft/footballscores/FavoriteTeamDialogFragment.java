package barqsoft.footballscores;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by ibalashov on 3/1/2016.
 */
public class FavoriteTeamDialogFragment extends DialogFragment {

    public static final String PREF_NAME = "team_name";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_fragment, null);
        builder.setView(layout)
                .setTitle(R.string.favorite_team_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //saving the variable to shared preferences

                        SharedPreferences sharedPref = PreferenceManager
                                .getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor =  sharedPref.edit();
                        EditText teamName = (EditText) layout.findViewById(R.id.favorite_team_name);
                        editor.putString(PREF_NAME, teamName.getText().toString());
                        editor.apply();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FavoriteTeamDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
