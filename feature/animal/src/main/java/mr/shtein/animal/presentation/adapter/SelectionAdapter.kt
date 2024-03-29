package mr.shtein.animal.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import mr.shtein.animal.R
import mr.shtein.model.FilterAutocompleteItem

class SelectionAdapter(
    context: Context, items: List<FilterAutocompleteItem>
) : ArrayAdapter<FilterAutocompleteItem>(
    context, R.layout.filter_selection_row, items
) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val currentView = inflater.inflate(R.layout.filter_selection_row, parent, false)

        val currentBreed: FilterAutocompleteItem = getItem(position) as FilterAutocompleteItem

        val label = currentView
            ?.findViewById<AppCompatTextView>(R.id.filter_selection_label)
        val img = currentView
            ?.findViewById<AppCompatImageView>(R.id.filter_selection_confirm_icon)

        label?.text = currentBreed.name
        if (currentBreed.isSelected) {
            img?.visibility = View.VISIBLE
        }
        return currentView!!
    }
}