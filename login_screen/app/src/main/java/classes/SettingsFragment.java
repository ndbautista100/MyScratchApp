package classes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.scratchappfeature.Login;
import com.example.scratchappfeature.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends PreferenceFragmentCompat implements DeleteAccountDialogFragment.DeleteAccountDialogListener, UpdateEmailDialogFragment.UpdateEmailDialogListener, EnterPasswordDialogFragment.EnterPasswordDialogListener {
    private static final String TAG = "SettingsFragment";
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        Preference feedbackPreference = findPreference("feedback");
        Preference emailPreference = findPreference("email");
        Preference passwordPreference = findPreference("password");
        Preference logoutPreference = findPreference("logout");
        Preference deleteAccountPreference = findPreference("delete");

        emailPreference.setSummary(auth.getCurrentUser().getEmail());

        emailPreference.setOnPreferenceClickListener(preference -> {
            // try to have the preference description display the user's current email in settings.xml
            EnterPasswordDialogFragment passwordDialog = new EnterPasswordDialogFragment();
            passwordDialog.show(getParentFragmentManager(), "EnterPasswordDialogFragment");

            return false;
        });

        passwordPreference.setOnPreferenceClickListener(preference -> {
            // TODO: implement

            return false;
        });

        feedbackPreference.setOnPreferenceClickListener(preference -> {
            String[] addresses = {"myscratch.longbeach@gmail.com"};

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, addresses);
            intent.putExtra(Intent.EXTRA_SUBJECT, "My Scratch! App - Feedback");
            startActivity(intent);

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

    @Override
    public void verifyPassword(String newPassword) {
        // do nothing - overridden in SettingsActivity
    }

    @Override
    public void applyNewEmail(String newEmail) {
        // do nothing - overridden in SettingsActivity
    }
}