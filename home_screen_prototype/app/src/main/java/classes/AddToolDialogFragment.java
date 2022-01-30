package classes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.scratchappfeature.CreateRecipeActivity;
import com.example.scratchappfeature.R;

public class AddToolDialogFragment extends AppCompatDialogFragment {

    public interface AddToolDialogListener {
        public void onDialogPositiveClick(androidx.fragment.app.DialogFragment dialog);
        public void onDialogNegativeClick(androidx.fragment.app.DialogFragment dialog);
    }

    AddToolDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddToolDialogListener) context;
        } catch ( ClassCastException e) {
            throw new ClassCastException(this.toString() + " must implement AddToolDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.activity_add_tool, null))
                .setPositiveButton(R.string.add_tool, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // add the tool
                        listener.onDialogPositiveClick(AddToolDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // cancel adding tool
                        listener.onDialogNegativeClick(AddToolDialogFragment.this);
                    }
                });

        return builder.create();
    }
}
