package classes;

import android.content.Intent;
import android.net.Uri;
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
import com.example.scratchappfeature.RecipePageActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends PreferenceFragmentCompat implements DeleteAccountDialogFragment.DeleteAccountDialogListener, UpdateEmailDialogFragment.UpdateEmailDialogListener {
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

        emailPreference.setOnPreferenceClickListener(preference -> {
            // try to have the preference description display the user's current email in settings.xml
            UpdateEmailDialogFragment dialog = new UpdateEmailDialogFragment();
            dialog.show(getParentFragmentManager(), "UpdateEmailDialogFragment");

            return false;
        });

        passwordPreference.setOnPreferenceClickListener(preference -> {
//            String newPassword = "new";
//            user.updatePassword(newPassword);

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
    public void applyNewEmail(String newEmail) {
        Log.d(TAG, "New email: " + newEmail);
        try {
            FirebaseUser user = auth.getCurrentUser();
            user.updateEmail(newEmail)
                .addOnSuccessListener(unused -> Toast.makeText(getContext(), "Email updated to " + newEmail, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update email", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.toString());
                });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
}