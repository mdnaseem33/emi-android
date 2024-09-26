import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.emitrackon.emi_trackon.R
import com.emitrackon.emi_trackon.dashboard.retailerModule.key.model.Brand

class SearchableSpinnerAdapter(
    context: Context,
    private val items: List<Brand>
) : ArrayAdapter<Brand>(context, 0, items), Filterable {

    private val originalItems = ArrayList(items)
    private val filteredItems = ArrayList(items)

    override fun getCount(): Int = filteredItems.size

    override fun getItem(position: Int): Brand = filteredItems[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_single_item, parent, false)
        val textView = view.findViewById<TextView>(R.id.text_view)
        textView.text = getItem(position).name
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = FilterResults()
                filteredItems.clear()
                if (constraint.isNullOrEmpty()) {
                    filteredItems.addAll(originalItems)
                } else {
                    val filterPattern = constraint.toString().lowercase().trim()
                    for (item in originalItems) {
                        if (item.name.lowercase().contains(filterPattern)) {
                            filteredItems.add(item)
                        }
                    }
                }
                filteredResults.values = filteredItems
                filteredResults.count = filteredItems.size
                return filteredResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }
}