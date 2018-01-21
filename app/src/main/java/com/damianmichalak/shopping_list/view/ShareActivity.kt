package com.damianmichalak.shopping_list.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import butterknife.ButterKnife
import com.damianmichalak.shopping_list.R
import com.damianmichalak.shopping_list.dagger.ActivityScope
import com.damianmichalak.shopping_list.helper.QRHelper
import com.damianmichalak.shopping_list.presenter.ShareActivityPresenter
import kotlinx.android.synthetic.main.activity_share.*
import rx.subscriptions.SerialSubscription
import rx.subscriptions.Subscriptions
import javax.inject.Inject

class ShareActivity : BaseActivity() {

    private val subscription = SerialSubscription()

    @Inject
    lateinit var presenter: ShareActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        initDagger()
        ButterKnife.bind(this)

        subscription.set(Subscriptions.from(
                presenter.currentListKeyObservable
                        .subscribe { listId -> QRHelper.generateQR(listId, activity_share_qr_view) },
                presenter.currentListNameObservable
                        .subscribe { listName -> activity_share_qr_list_name.text = listName }
        ))

        val supportActionBar = supportActionBar
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true)
            supportActionBar.setTitle(R.string.activity_share_title)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        subscription.set(Subscriptions.empty())
    }

    override fun initDagger() {
        val component = DaggerShareActivity_Component
                .builder()
                .applicationComponent(applicationComponent)
                .build()
        component.inject(this)
    }

    @ActivityScope
    @dagger.Component(dependencies = arrayOf(MainApplication.ApplicationComponent::class))
    interface Component {

        fun inject(activity: ShareActivity)

    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, ShareActivity::class.java)
        }
    }
}
