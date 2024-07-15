import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.appmenubutton.R

class ListFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var arrayList: ArrayList<String>
    private lateinit var filteredList: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var toolbar: Toolbar
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // handle arguments if needed
        }
        setHasOptionsMenu(true) // Enable options menu for this fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        // Initialize views
        listView = view.findViewById(R.id.lstAlumnos)
        toolbar = view.findViewById(R.id.toolbar)

        // Set toolbar in the hosting activity
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)

        // Populate list items
        val items = resources.getStringArray(R.array.alumnos)
        arrayList = ArrayList(items.toList())
        filteredList = ArrayList(arrayList)

        adapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, filteredList) {
            override fun getFilter(): Filter {
                return object : Filter() {
                    override fun performFiltering(constraint: CharSequence?): FilterResults {
                        val filterResults = FilterResults()
                        if (constraint.isNullOrEmpty()) {
                            filterResults.values = arrayList
                            filterResults.count = arrayList.size
                        } else {
                            val queryParts = constraint.toString().lowercase().trim().split("\\s+".toRegex())
                            val results = arrayList.filter { item ->
                                queryParts.all { queryPart ->
                                    item.lowercase().contains(queryPart)
                                }
                            }
                            filterResults.values = results
                            filterResults.count = results.size
                        }
                        return filterResults
                    }

                    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                        if (results != null && results.values is List<*>) {
                            filteredList.clear()
                            filteredList.addAll(results.values as List<String>)
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }
        listView.adapter = adapter

        // Handle item click
        listView.setOnItemClickListener { parent, view, position, id ->
            val alumno: String = parent.getItemAtPosition(position).toString()
            showAlertDialog("$position: $alumno")
        }

        return view
    }

    private fun showAlertDialog(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Lista de Alumnos")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, which ->
                // Handle OK button click if needed
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu) // Inflate your menu items

        // Find the search item in your menu
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater) // Call super after inflating
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}