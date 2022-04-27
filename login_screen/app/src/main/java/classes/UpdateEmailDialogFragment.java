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
public class UpdateEmailDialogFragment extends AppCompatDialogFragment {
    private EditText emailEditText;
    private UpdateEmailDialogListener listener;

    public interface UpdateEmailDialogListener {
        void applyNewEmail(String newEmail);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (UpdateEmailDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement UpdateEmailDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_update_email, null);

        builder.setView(view)
            .setPositiveButton("Update", (dialogInterface, i) -> {
                // update email
                listener.applyNewEmail(emailEditText.getText().toString());
            })
            .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                // cancel updating email
            });

        emailEditText = view.findViewById(R.id.updateEmailEditText);

        return builder.create();
    }
}
