package classes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.scratchappfeature.Login;
import com.example.scratchappfeature.R;
import com.example.scratchappfeature.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

public class DeleteAccountDialogFragment extends DialogFragment {
    private static final String TAG = "DeleteAccountDialogFrag";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private Context context;
    private String userID = auth.getCurrentUser().getUid();

    private DeleteAccountDialogListener listener;

    public interface DeleteAccountDialogListener {
        void startLoginActivity();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;

        try {
            listener = (DeleteAccountDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement DeleteAccountDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_account)
                .setPositiveButton(R.string.delete, (dialogInterface, i) ->
                        FirebaseAuth.getInstance().getCurrentUser().delete()
                                .addOnCompleteListener(task -> {
                                    Toast.makeText(context, "Account successfully deleted.", Toast.LENGTH_SHORT).show();
                                    Log.i(TAG, "User account successfully deleted.");

                                    // now remove it from profiles database
                                    db.collection("profile").document(userID).delete()
                                            .addOnSuccessListener(unused -> {
                                                Log.i(TAG, "Profile DocumentSnapshot successfully deleted!");
                                                listener.startLoginActivity();
                                            })
                                            .addOnFailureListener(e -> Log.e(TAG, "Error deleting profile document", e));
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to delete account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, e.toString());
                                })
                ).setNegativeButton(R.string.cancel, (dialog, id) -> {
            // User cancelled the dialog - do nothing
        });

        return builder.create();
    }
}