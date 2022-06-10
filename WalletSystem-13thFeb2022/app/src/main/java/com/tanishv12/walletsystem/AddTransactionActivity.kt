package com.tanishv12.walletsystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_add_transaction.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.app.DatePickerDialog
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_add_transaction.amountinput
import kotlinx.android.synthetic.main.activity_add_transaction.amountlayout
import kotlinx.android.synthetic.main.activity_add_transaction.closebtn
import kotlinx.android.synthetic.main.activity_add_transaction.descriptioninput
import kotlinx.android.synthetic.main.activity_add_transaction.labelinput
import kotlinx.android.synthetic.main.activity_add_transaction.labellayout
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity()
{
    private lateinit var inputdatePicker: TextView
    private lateinit var btnDatePicker: Button
    private lateinit var addTransactionBtn: Button
    private lateinit var transaction: Transaction
    private var isDetailed: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        if(intent.getSerializableExtra("transaction") != null)
        {
            isDetailed = true;
            transaction = intent.getSerializableExtra("transaction") as Transaction
            addTransactionBtn = findViewById(R.id.addtransactionbtn)
            addTransactionBtn.setText("UPDATE")

            labelinput.setText(transaction.label)
            amountinput.setText(transaction.amount.toString())
            descriptioninput.setText(transaction.description)
            dateinput.setText(transaction.date)

            labelinput.addTextChangedListener {
                addTransactionBtn.visibility = View.VISIBLE
                if(it!!.count()>0)
                    labellayout.error = null
            }

            dateinput.addTextChangedListener {
                addTransactionBtn.visibility = View.VISIBLE
                if(it!!.count()>0)
                    datelayout.error = null
            }

            amountinput.addTextChangedListener {
                addTransactionBtn.visibility = View.VISIBLE
                if(it!!.count()>0)
                    amountlayout.error = null
            }

            descriptioninput.addTextChangedListener {
                addTransactionBtn.visibility = View.VISIBLE
            }

        }
        else
        {
            isDetailed = false;
        }

        inputdatePicker = findViewById(R.id.dateinput)
        btnDatePicker = findViewById(R.id.btndatepicker)

        val myCalendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayofMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayofMonth)
            updatelabel(myCalendar)
        }

        btnDatePicker.setOnClickListener {
            DatePickerDialog(this, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        labelinput.addTextChangedListener {
            if(it!!.count()>0)
                labellayout.error = null
        }

        dateinput.addTextChangedListener {
            if(it!!.count()>0)
                datelayout.error = null
        }

        amountinput.addTextChangedListener {
            if(it!!.count()>0)
                amountlayout.error = null
        }

        addtransactionbtn.setOnClickListener {
            val label = labelinput.text.toString()
            val date = dateinput.text.toString()
            val amount = amountinput.text.toString().toDoubleOrNull()
            val description = descriptioninput.text.toString()

            if(label.isEmpty()) {
                labellayout.error = "Please enter a valid name"
            }
            else if (amount==null) {    
                amountlayout.error = "Please enter a valid amount"
            }
            else if (date.isEmpty()){
                datelayout.error = "Please enter a valid date"
            }
            else {
                if (!isDetailed)
                {
                    val transaction = Transaction(0, label, amount, date, description)
                    insert(transaction)
                }
                else
                {
                    val transaction = Transaction(transaction.id, label, amount, date,description)
                    update(transaction)
                }
            }
        }
        closebtn.setOnClickListener {
            finish()
        }
    }

    private fun updatelabel(myCalendar: Calendar)
    {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        inputdatePicker.setText(sdf.format(myCalendar.time))
    }

    private fun insert(transaction: Transaction)
    {
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "transactions").build()
        GlobalScope.launch {
            db.transactionDao().insertAll(transaction)
            finish()
        }
    }

    private fun update(transaction: Transaction)
    {
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "transactions").build()
        GlobalScope.launch {
            db.transactionDao().update(transaction)
            finish()
        }
    }
}