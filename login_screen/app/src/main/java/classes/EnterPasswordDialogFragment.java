package classes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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
public class EnterPasswordDialogFragment extends AppCompatDialogFragment {
    private EditText passwordEditText;
    private EnterPasswordDialogListener listener;

    public interface EnterPasswordDialogListener {
        void verifyPassword(String newPassword);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (EnterPasswordDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement EnterPasswordDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_enter_password, null);

        builder.setView(view)
            .setPositiveButton("OK", (dialogInterface, i) -> {
                // verify password
                listener.verifyPassword(passwordEditText.getText().toString().trim());
            })
            .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                // cancel entering password
            });

        passwordEditText = view.findViewById(R.id.enterPasswordEditText);

        return builder.create();
    }
}
