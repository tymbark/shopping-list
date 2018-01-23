package com.damianmichalak.shopping_list.view

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.widget.Toast
import butterknife.ButterKnife
import com.damianmichalak.shopping_list.R
import com.damianmichalak.shopping_list.dagger.ActivityScope
import com.damianmichalak.shopping_list.helper.guava.Strings
import com.damianmichalak.shopping_list.presenter.MainActivityPresenter
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import rx.subscriptions.SerialSubscription
import rx.subscriptions.Subscriptions
import javax.inject.Inject

class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var presenter: MainActivityPresenter

    private val drawerToggle: ActionBarDrawerToggle by lazy {
        ActionBarDrawerToggle(
                this,
                main_drawer_layout,
                R.string.drawer_open,
                R.string.drawer_close

        )
    }
    private val subscription = SerialSubscription()
    private val shoppingListFragment = ShoppingListFragment.newInstance()
    private val productsListFragment = ProductsListFragment.newInstance()
    private val historyFragment = HistoryFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initDagger()

        ButterKnife.bind(this)

        setupDrawerAndToolbar()
        navigation.setOnNavigationItemSelectedListener(this)

        subscription.set(Subscriptions.from(
                presenter.closeDrawerObservable
                        .subscribe { main_drawer_layout.closeDrawers() },
                presenter.currentOpenedFragment
                        .subscribe { tag ->
                            if (Strings.isNullOrEmpty(tag)) {
                                navigation!!.selectedItemId = R.id.navigation_shopping_list
                            } else if (tag == ProductsListFragment.TAG) {
                                navigation!!.selectedItemId = R.id.navigation_products
                            } else if (tag == ShoppingListFragment.TAG) {
                                navigation!!.selectedItemId = R.id.navigation_shopping_list
                            } else if (tag == HistoryFragment.TAG) {
                                navigation!!.selectedItemId = R.id.navigation_history
                            }
                        },
                presenter.qrCodeListError
                        .subscribe { Snackbar.make(main_root_view, R.string.main_activity_list_dont_exist_after_qr, Snackbar.LENGTH_LONG).show() },
                presenter.qrCodeListSuccess
                        .subscribe { s -> Snackbar.make(main_root_view, getString(R.string.main_activity_list_added_qr, s), Snackbar.LENGTH_LONG).show() },
                presenter.showWelcomeScreenObservable
                        .subscribe {
                            finish()
                            startActivity(WelcomeActivity.newIntent(this@MainActivity))
                        },
                presenter.subscription
        ))

    }

    private fun setupDrawerAndToolbar() {
        main_drawer_layout.addDrawerListener(drawerToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    //    todo add options
    //    @Override
    //    public boolean onCreateOptionsMenu(Menu menu) {
    //        getMenuInflater().inflate(R.menu.shopping_list_share, menu);
    //        return true;
    //    }

    fun setToolbarTitle(title: String?) {
        supportActionBar?.title = if (Strings.isNullOrEmpty(title)) getString(R.string.app_name) else title
    }

    fun setToolbarSubtitle(subtitle: String?) {
        supportActionBar?.subtitle = Strings.nullToEmpty(getString(R.string.main_activity_toolbar_subtitle, subtitle))
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription.set(Subscriptions.empty())
        main_drawer_layout.removeDrawerListener(drawerToggle)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, R.string.scanner_failed, Toast.LENGTH_LONG).show()
            } else {
                presenter.qrCodeShoppingListSubject.onNext(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun selectItemNavigation(item: Int) {
        if (navigation != null) {
            navigation!!.selectedItemId = item
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_shopping_list -> {
                supportFragmentManager.beginTransaction().replace(R.id.content, shoppingListFragment, ShoppingListFragment.TAG).commit()
                presenter.currentOpenedFragmentSubject.onNext(ShoppingListFragment.TAG)
                return true
            }
            R.id.navigation_products -> {
                supportFragmentManager.beginTransaction().replace(R.id.content, productsListFragment, ProductsListFragment.TAG).commit()
                presenter.currentOpenedFragmentSubject.onNext(ProductsListFragment.TAG)
                return true
            }
            R.id.navigation_history -> {
                supportFragmentManager.beginTransaction().replace(R.id.content, historyFragment, HistoryFragment.TAG).commit()
                presenter.currentOpenedFragmentSubject.onNext(HistoryFragment.TAG)
                return true
            }
        }
        return false
    }

    //this is needed not to close MainActivity after opening drawer
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return drawerToggle.onOptionsItemSelected(item)
    }

    override fun initDagger() {
        val component = DaggerMainActivity_Component
                .builder()
                .applicationComponent(applicationComponent)
                .build()
        component.inject(this)
    }

    @ActivityScope
    @dagger.Component(dependencies = arrayOf(MainApplication.ApplicationComponent::class), modules = arrayOf())
    interface Component {

        fun inject(activity: MainActivity)

    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
