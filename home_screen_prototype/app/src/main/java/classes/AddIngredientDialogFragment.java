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

import com.example.scratchappfeature.R;

/**
 * DialogFragment that pops up when a user wants to add an ingredient to a recipe
 */
public class AddIngredientDialogFragment extends AppCompatDialogFragment {
    public interface AddIngredientDialogListener {
        public void onIngredientDialogPositiveClick(androidx.fragment.app.DialogFragment dialog);
        public void onIngredientDialogNegativeClick(androidx.fragment.app.DialogFragment dialog);
    }

    AddIngredientDialogListener listener;
    String ingredientName;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddIngredientDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(this.toString() + " must implement AddIngredientDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.activity_add_ingredient, null))
                .setPositiveButton(R.string.add_ingredient, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // add the ingredient
                        listener.onIngredientDialogPositiveClick(AddIngredientDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // cancel adding ingredient
                        listener.onIngredientDialogNegativeClick(AddIngredientDialogFragment.this);
                    }
                });

        return builder.create();
    }
}
