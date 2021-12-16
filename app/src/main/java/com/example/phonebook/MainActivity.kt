package com.example.phonebook

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.phonebook.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var txtName: MaterialTextView
    lateinit var layoutName: LinearLayout
    lateinit var inputName: TextInputEditText
    lateinit var inputNum: TextInputEditText
    lateinit var inputGender: TextInputEditText
    lateinit var inputPhone: TextInputEditText
    private lateinit var cursor: Cursor
    lateinit var btnCreateDB: MaterialButton
    lateinit var btnPrev: MaterialButton
    private lateinit var btnNext: MaterialButton
    lateinit var btnDelete: MaterialButton
    lateinit var btnUpdate: MaterialButton
    lateinit var btnCreate: MaterialButton
    lateinit var db: SQLiteDatabase
    lateinit var dbConn: DBConnection
    private var idThis by Delegates.notNull<Int>()
    private val TABLE_NAME = "PhoneBook1"
    private val ID = "id"
    private val NAME = "name"
    private val SEX = "sex"
    private val PHONE = "phone"
    private var statusFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        // 绑定视图
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 绑定toolbar
        setSupportActionBar(binding.toolbar)

        // 绑定路由
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // 绑定组件
        layoutName = findViewById(R.id.layout_name)
        txtName = findViewById(R.id.text_view_contact)
        inputName = findViewById(R.id.input_stu_name)
        inputNum = findViewById(R.id.input_stu_num)
        inputGender = findViewById(R.id.input_stu_gender)
        inputPhone = findViewById(R.id.input_stu_phone)
        btnCreateDB = findViewById(R.id.btn_create_db)
        btnPrev = findViewById(R.id.btn_prev)
        btnNext = findViewById(R.id.btn_next)
        btnDelete = findViewById(R.id.btn_delete)
        btnUpdate = findViewById(R.id.btn_update)
        btnCreate = findViewById(R.id.btn_add)

        // 绑定事件监听
        btnCreateDB.setOnClickListener { getDBConnection() }
        btnPrev.setOnClickListener { prev() }
        btnNext.setOnClickListener { next() }
        btnDelete.setOnClickListener { deleteClickEvent() }
        btnUpdate.setOnClickListener { updateClickEvent() }
        btnCreate.setOnClickListener { addClickEvent() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        // 关闭数据库连接
        try {
            cursor.close()
            db.close()
            Toast.makeText(this, "已关闭数据库连接", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "关闭数据库连接失败${e.message}", Toast.LENGTH_LONG).show()
        }
        super.onDestroy()
    }

    // 自定义方法
    // 获取数据库链接
    private fun getDBConnection() {
        try {
            dbConn = DBConnection(this@MainActivity)
            db = dbConn.writableDatabase
            SQLiteDatabase.openOrCreateDatabase(DBConnection.DB_NAME, null)
            cursor = db.query(TABLE_NAME, null, null, null,
                null, null, null)
            cursor.moveToNext()
            Toast.makeText(this, "已连接数据库", Toast.LENGTH_LONG).show()
            // 修改连接按钮状态
            btnCreateDB.isEnabled = false
            btnCreateDB.text = "已连接"
            firstTimeGetDB()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "连接数据库失败${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // 首次初始化
    private fun firstTimeGetDB() {
        if (cursor.count == 0 || cursor.count == 1) {
            if (!statusFlag) {
                statusFlag = true
                layoutName.visibility = View.VISIBLE
            }
        }
        dataShow()
    }

    // 展示数据
    private fun dataShow() {
        try {
            idThis = if (cursor.getString(0).toInt() >= 0){
                cursor.getString(0).toInt()
            } else { 1 }
        } catch (e: Exception) {
            Toast.makeText(this, "出错${e.message}", Toast.LENGTH_LONG).show()
        }

        val thisId: String = cursor.getString(0)
        val thisStuName: String = cursor.getString(1)
        val thisGender: String = cursor.getString(2)
        val thisPhone: String = cursor.getString(3)

        txtName.text = thisStuName
        inputName.setText(thisStuName)
        inputNum.setText(thisId)
        inputGender.setText(thisGender)
        inputPhone.setText(thisPhone)
    }

    // 上一页按钮事件
    private fun prev() {
        try {
            if (cursor.isFirst) {
                cursor.moveToLast()
            } else {
                cursor.moveToPrevious()
            }
            dataShow()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "上一页失败${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // 下一个按钮事件
    private fun next() {
        try {
            if (cursor.isLast) {
                cursor.moveToFirst()
            } else {
                cursor.moveToNext()
            }
            dataShow()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "下一页失败${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // 增加按钮事件
    private fun addClickEvent() {
        if (!statusFlag) {
            layoutName.visibility = View.VISIBLE
            txtName.text = resources.getText(R.string.txt_defaultContact)
            inputName.setText("")
            inputNum.setText("")
            inputGender.setText("")
            inputPhone.setText("")

            statusFlag = true
            Toast.makeText(this, "填写完成后请再次点击新增按钮", Toast.LENGTH_LONG).show()
        } else {
            addOne()
            statusFlag = false
            // 重新获取表单数据并刷新
            reGetData()
            dataShow()
        }
    }

    // 删除按钮事件
    private fun deleteClickEvent() {
        delete()
        // 重新获取表单数据并刷新
        reGetData()
        dataShow()
    }

    // 更新按钮事件
    private fun updateClickEvent() {
        if (!statusFlag) {
            // 组件属性
            layoutName.visibility = View.VISIBLE

            statusFlag = true
            Toast.makeText(this, "修改完成后请再次点击修改按钮", Toast.LENGTH_LONG).show()
        } else {
            update()
            statusFlag = false
            // 重新获取表单数据并刷新
            reGetData()
            dataShow()
        }
    }

    // 刷新表单数据
    private fun reGetData() {
        db.close()
        cursor.close()
        db = dbConn.writableDatabase
        SQLiteDatabase.openOrCreateDatabase(DBConnection.DB_NAME, null)
        cursor = db.query(TABLE_NAME, null, null, null,
            null, null, null)
        next()
        prev()
    }

    // 增加
    private fun addOne() {
        val values = ContentValues()
        val stuName: String = inputName.text.toString()
        val stuNum: String = inputNum.text.toString()
        val stuGender: String = inputGender.text.toString()
        val stuPhone: String = inputPhone.text.toString()
        values.put(NAME, stuName)
        values.put(ID, stuNum)
        values.put(SEX, stuGender)
        values.put(PHONE, stuPhone)

        try {
            val db2: SQLiteDatabase = dbConn.writableDatabase
            db2.insert(TABLE_NAME, null, values)
            db2.close()
            Toast.makeText(this, "添加成功", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "添加失败${e.message}", Toast.LENGTH_LONG).show()
        }

    }

    // 删除
    private fun delete() {
        val whereClause = "$ID=$idThis"

        try {
            val db2: SQLiteDatabase = dbConn.writableDatabase
            db2.delete(TABLE_NAME, whereClause, null)
            db2.close()
            Toast.makeText(this, "删除成功", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "删除失败${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // 更新
    private fun update() {
        val values = ContentValues()
        val whereClause = "$ID=$idThis"

        val stuName: String = inputName.text.toString()
        val stuNum: String = inputNum.text.toString()
        val stuGender: String = inputGender.text.toString()
        val stuPhone: String = inputPhone.text.toString()
        values.put(NAME, stuName)
        values.put(ID, stuNum)
        values.put(SEX, stuGender)
        values.put(PHONE, stuPhone)

        try {
            val db2: SQLiteDatabase = dbConn.writableDatabase
            db2.update(TABLE_NAME, values, whereClause, null)
            db2.close()
            Toast.makeText(this, "更新成功", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "更新失败${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}