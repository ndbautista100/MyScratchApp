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
 * DialogFragment that pops up when a user wants to add an ingredient to a recipe
 */
public class AddIngredientDialogFragment extends AppCompatDialogFragment {
    private EditText ingredientNameEditText;
    private AddIngredientDialogListener listener;

    public interface AddIngredientDialogListener {
        void applyIngredientName(String ingredientName);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddIngredientDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement AddIngredientDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_add_ingredient, null);

        builder.setView(view)
                .setPositiveButton(R.string.add_ingredient, (dialogInterface, i) -> {
                    // add the ingredient
                    String ingredientName = ingredientNameEditText.getText().toString();
                    listener.applyIngredientName(ingredientName);
                })
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                    // cancel adding ingredient
                });

        ingredientNameEditText = view.findViewById(R.id.addIngredientNameEditText);

        return builder.create();
    }
}
