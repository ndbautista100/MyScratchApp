package classes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.example.scratchappfeature.Login;
import com.example.scratchappfeature.MainActivity;
import com.example.scratchappfeature.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends PreferenceFragmentCompat implements DeleteAccountDialogFragment.DeleteAccountDialogListener {
    private static final String TAG = "SettingsFragment";

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        Preference feedbackPreference = findPreference("feedback");
        Preference logoutPreference = findPreference("logout");
        Preference deleteAccountPreference = findPreference("delete");

        feedbackPreference.setOnPreferenceClickListener(preference -> {
            Log.d(TAG, "TODO: Implement feedback feature (open email app to myscratch.longbeach@gmail.com, etc.");

            return false;
        });

        logoutPreference.setOnPreferenceClickListener(preference -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), Login.class));
            Toast.makeText(getContext(), "Successfully logged out.", Toast.LENGTH_SHORT).show();

            return true;
        });

        deleteAccountPreference.setOnPreferenceClickListener(preference -> {
            DeleteAccountDialogFragment dialog = new DeleteAccountDialogFragment();
            dialog.show(getParentFragmentManager(), "DeleteAccountDialogFragment");

            return false;
        });
    }

    @Override
    public void startLoginActivity() {
        startActivity(new Intent(getContext(), Login.class));
    }
}