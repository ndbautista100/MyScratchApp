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
public class AddIngredientListDialog extends AppCompatDialogFragment {
    private EditText ingredientNameET;
    private EditText ingredientCategoryET;
    private AddIngredientListDialogListener listener;

    public interface AddIngredientListDialogListener {
        void saveIngredient(String ingredientNameAndCategory);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddIngredientListDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement AddIngredientListDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_add_ingredient_to_list, null);

        builder.setView(view)
                .setPositiveButton(R.string.add_ingredient, (dialogInterface, i) -> {
                    // add the ingredient
                    String ingredientName = ingredientNameET.getText().toString();
                    String ingredientCategory = ingredientCategoryET.getText().toString();
                    listener.saveIngredient(ingredientName + "/" + ingredientCategory);
                })
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                    // cancel adding ingredient
                });

        ingredientNameET = view.findViewById(R.id.ingredientListNameET);
        ingredientCategoryET = view.findViewById(R.id.ingredientsListCategoryET);

        return builder.create();
    }
}
