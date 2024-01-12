package net.invictusmanagement.invictuslifestyle.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_notificiation_read_unread.*
import net.invictusmanagement.invictuslifestyle.R
import net.invictusmanagement.invictuslifestyle.adapters.NotificationRecipientAdapter
import net.invictusmanagement.invictuslifestyle.models.NotificationRecipient
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack
import net.invictusmanagement.invictuslifestyle.webservice.WSException
import net.invictusmanagement.invictuslifestyle.webservice.WebService

class NotificationReadUnread : AppCompatActivity() {
    private var id: Long = 0
    private var recipientData: NotificationRecipient = NotificationRecipient()
    private var adapter: NotificationRecipientAdapter? = null
    private var titleList: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificiation_read_unread)

        setToolBar()
        initView()
    }

    private fun setToolBar() {
        toolbar.title = "Read/Unread Count"
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_action_back)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun initView() {
        id = intent.extras?.get("ID") as Long
        callAPIGetRecipient()
    }

    private fun callAPIGetRecipient() {
        WebService.getInstance().getNotificationRecipient(id, object : RestCallBack<NotificationRecipient?> {
            override fun onResponse(response: NotificationRecipient?) {
                if (response != null) {
                    recipientData = response
                    setData()
                }
            }

            override fun onFailure(wse: WSException?) {

            }

        })
    }

    private fun setData() {
        tvTotalRecipient.text = (recipientData.readRecipientCount + recipientData.unreadRecipientCount).toString()

        val expandableListDetail = HashMap<String, List<String>>()
        expandableListDetail.set("Read Recipients: (" + recipientData.readRecipientCount + ")",
                recipientData.readRecipientList.toList())
        expandableListDetail.set("Unread Recipients: (" + recipientData.unreadRecipientCount + ")",
                recipientData.unreadRecipientList.toList())

        titleList = ArrayList(expandableListDetail.keys)
        adapter = NotificationRecipientAdapter(this, titleList as ArrayList<String>, expandableListDetail)
        expandableList.setAdapter(adapter)

    }
}