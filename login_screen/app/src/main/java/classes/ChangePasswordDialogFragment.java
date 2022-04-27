package classes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.scratchappfeature.R;

/**
 * DialogFragment that pops up when a user wants to add an ingredient to a recipe
 */
public class ChangePasswordDialogFragment extends AppCompatDialogFragment {
    private EditText currentPasswordEditText;
    private EditText newPasswordEditText;
    private EditText confirmNewPasswordEditText;

    private ChangePasswordDialogListener listener;

    public interface ChangePasswordDialogListener {
        void changePassword(String currentPassword, String newPassword, String confirmPassword);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ChangePasswordDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement ChangePasswordDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_change_password, null);

        builder.setView(view)
            .setPositiveButton("OK", (dialogInterface, i) -> {
                // change password
                listener.changePassword(currentPasswordEditText.getText().toString().trim(), newPasswordEditText.getText().toString().trim(), confirmNewPasswordEditText.getText().toString().trim());
            })
            .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                // cancel changing password
            });

        currentPasswordEditText = view.findViewById(R.id.enterPasswordEditText);
        newPasswordEditText = view.findViewById(R.id.newPasswordEditText);
        confirmNewPasswordEditText = view.findViewById(R.id.confirmNewPasswordEditText);

        return builder.create();
    }
}
