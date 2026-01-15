package com.dzadafa.mywallet.ui.investment

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzadafa.mywallet.MyWalletApplication
import com.dzadafa.mywallet.MyWalletViewModelFactory
import com.dzadafa.mywallet.R
import com.dzadafa.mywallet.adapter.InvestmentLogAdapter
import com.dzadafa.mywallet.data.InvestmentLog
import com.dzadafa.mywallet.databinding.ActivityInvestmentDetailBinding
import com.dzadafa.mywallet.databinding.DialogAddInvestmentTransactionBinding
import com.dzadafa.mywallet.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class InvestmentDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInvestmentDetailBinding
    private val viewModel: InvestmentDetailViewModel by viewModels {
        MyWalletViewModelFactory(
            (application as MyWalletApplication).transactionRepository,
            (application as MyWalletApplication).wishlistRepository,
            (application as MyWalletApplication).budgetRepository,
            (application as MyWalletApplication).investmentRepository,
            application
        )
    }

    private var investmentId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvestmentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        investmentId = intent.getIntExtra("INVESTMENT_ID", 0)
        if (investmentId == 0) {
            finish()
            return
        }

        setupRecyclerView()
        setupListeners()
        viewModel.loadInvestment(investmentId)

        viewModel.investment.observe(this) { inv ->
            if (inv != null) {
                supportActionBar?.title = inv.name
                binding.tvTotalHoldings.text = "${Utils.formatDecimal(inv.amountHeld)} ${inv.type}"
                binding.tvCurrentValue.text = "≈ ${Utils.formatAsRupiah(inv.getCurrentValue())}"
                binding.tvAvgPrice.text = Utils.formatAsRupiah(inv.averageBuyPrice)

                val pl = inv.getProfitLossPercentage()
                val plStr = String.format("%.2f%%", pl)
                binding.tvUnrealizedPl.text = if (pl >= 0) "+$plStr" else plStr

                val color = if (pl >= 0) Color.parseColor("#4CAF50") else Color.parseColor("#F44336")
                binding.tvUnrealizedPl.setTextColor(color)
            } else {

                finish()
            }
        }
    }

    private fun setupRecyclerView() {

        val adapter = InvestmentLogAdapter { log ->
            showDeleteLogConfirmation(log)
        }
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter

        viewModel.logs.observe(this) { list ->
            adapter.submitList(list)
        }
    }

    private fun showDeleteLogConfirmation(log: InvestmentLog) {
        AlertDialog.Builder(this)
            .setTitle("Delete Record")
            .setMessage("Are you sure? Your holdings and average price will be recalculated from scratch.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteLog(log)
                Toast.makeText(this, "Record deleted & Recalculated", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_investment_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_update_price -> {
                showUpdatePriceDialog()
                true
            }
            R.id.action_delete -> {
                showDeleteConfirmation()
                true
            }
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showUpdatePriceDialog() {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.hint = "New Price (Rp)"

        val container = FrameLayout(this)
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, 
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = 50
        params.rightMargin = 50
        input.layoutParams = params
        container.addView(input)

        AlertDialog.Builder(this)
            .setTitle("Update Market Price")
            .setMessage("Enter the new current price per unit.")
            .setView(container)
            .setPositiveButton("Update") { _, _ ->
                val newPrice = input.text.toString().toDoubleOrNull()
                if (newPrice != null && newPrice >= 0) {
                    viewModel.updatePrice(newPrice)
                    Toast.makeText(this, "Price Updated", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Asset")
            .setMessage("Are you sure you want to delete this asset? All history logs will also be deleted.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteInvestment()
                Toast.makeText(this, "Asset Deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupListeners() {
        binding.fabBuy.setOnClickListener { showTransactionDialog("BUY") }
        binding.fabSell.setOnClickListener { showTransactionDialog("SELL") }
    }

    private fun showTransactionDialog(type: String) {
        val dialogBinding = DialogAddInvestmentTransactionBinding.inflate(layoutInflater)
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        dialogBinding.etDate.setText(dateFormat.format(calendar.time))

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val units = dialogBinding.etUnits.text.toString().toDoubleOrNull() ?: 0.0
                val price = dialogBinding.etPricePerUnit.text.toString().toDoubleOrNull() ?: 0.0
                dialogBinding.tvTotalSpent.text = "Total: ${Utils.formatAsRupiah(units * price)}"
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        dialogBinding.etUnits.addTextChangedListener(watcher)
        dialogBinding.etPricePerUnit.addTextChangedListener(watcher)

        dialogBinding.etDate.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                calendar.set(year, month, day)
                dialogBinding.etDate.setText(dateFormat.format(calendar.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        AlertDialog.Builder(this)
            .setTitle("$type Asset")
            .setView(dialogBinding.root)
            .setPositiveButton("Confirm") { _, _ ->
                val units = dialogBinding.etUnits.text.toString().toDoubleOrNull()
                val price = dialogBinding.etPricePerUnit.text.toString().toDoubleOrNull()

                if (units != null && price != null && units > 0) {
                    viewModel.addTransaction(type, calendar.time, units, price)
                } else {
                    Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
