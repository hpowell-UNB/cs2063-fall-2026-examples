package mobiledev.unb.ca.roompersistencelibrarydemo

import android.os.Bundle
import android.view.WindowManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import mobiledev.unb.ca.roompersistencelibrarydemo.db.AppDatabase
import mobiledev.unb.ca.roompersistencelibrarydemo.db.entities.Item
import mobiledev.unb.ca.roompersistencelibrarydemo.ui.ItemListAdapter
import mobiledev.unb.ca.roompersistencelibrarydemo.repositories.ItemRepository
import mobiledev.unb.ca.roompersistencelibrarydemo.ui.ItemViewModel
import mobiledev.unb.ca.roompersistencelibrarydemo.ui.ItemViewModelFactory

class MainActivity : AppCompatActivity() {
    // Attribute for the item view model
    private val itemViewModel: ItemViewModel by viewModels {
        // Use the factory lambda to inject the repository directly
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ItemRepository(database.itemDao())

        // Return the configured factory
        ItemViewModelFactory(repository)
    }
    // Attribute for item list adapter used for the results
    private lateinit var itemAdapter: ItemListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Force light mode when running the app
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Update the action bar title
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.apply {
            title = getString(R.string.item_list)
        }

        // Set the RecyclerView for use
        setupRecyclerView()

        // Setup listener to observe state changes
        observeUiState()

        // Add the listener events
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            handleFabButtonPressed()
        }
    }

    private fun setupRecyclerView() {
        itemAdapter = ItemListAdapter { itemToDelete ->
            handleDeleteItemButtonPressed(itemToDelete)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.apply {
            adapter = itemAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun observeUiState() {
        // Start a coroutine in the lifecycle scope
        lifecycleScope.launch {
            // repeatOnLifecycle executes the block when the lifecycle is at least STARTED
            // and automatically cancels it when the lifecycle moves to STOPPED
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                itemViewModel.uiState.collect { items ->
                    // Submit the list to the ListAdapter
                    itemAdapter.submitList(items)
                }
            }
        }
    }

    private fun handleFabButtonPressed() {
        val builder = AlertDialog.Builder(this@MainActivity)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_item, null)

        builder.setView(dialogView)
            .setPositiveButton(getString(R.string.dialog_button_add)) { dialog, _ ->
                val itemEditText: EditText = dialogView.findViewById(R.id.item_edit_text)
                val numEditText: EditText = dialogView.findViewById(R.id.number_edit_text)
                val item = itemEditText.text.toString()
                val num = numEditText.text.toString()
                addItem(item, num)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.dialog_button_cancel)) { dialog, _ ->
                dialog.cancel()
            }

        val alertDialog = builder.create()
        alertDialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        alertDialog.show()
    }

    fun handleDeleteItemButtonPressed(item: Item) {
        val builder = AlertDialog.Builder(this@MainActivity)

        builder.setTitle(getString(R.string.confirm_delete_title))
        builder.setMessage(getString(R.string.confirm_delete_message))

        builder.setPositiveButton(getString(R.string.dialog_button_delete)) { dialog, _ ->
            deleteItem(item)
            dialog.dismiss()
        }

        builder.setNegativeButton(getString(R.string.dialog_button_cancel)) { dialog, _ ->
            dialog.cancel()
        }

        // Create and show the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun addItem(item: String, num: String) {
        itemViewModel.insert(item, num.toInt())
    }

    private fun deleteItem(item: Item) {
        itemViewModel.delete(item)
    }
}