package classes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.scratchappfeature.R;

/**
 * DialogFragment that pops up when a user wants to add a tool to a recipe
 */
public class AddToolDialogFragment extends AppCompatDialogFragment {
    private EditText toolNameEditText;
    private AddToolDialogListener listener;

    public interface AddToolDialogListener {
        void applyToolName(String toolName);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddToolDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AddToolDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_add_tool, null);

        builder.setView(view)
                .setPositiveButton(R.string.add_tool, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // add the tool
                        String toolName = toolNameEditText.getText().toString();
                        listener.applyToolName(toolName);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // cancel adding tool
                    }
                });

        toolNameEditText = view.findViewById(R.id.addToolNameEditText);

        return builder.create();
    }
}
