package com.skillbox.ascent.main
//логика работы напоминаний(можем вкл или откл,при первом запуске включены):чтобы точно сработало
// создаем OneTimeWorkManager на опреленное время(время напоминаний можно установить, по умолчанию
// установлены в 19:30)->после завершения задачи запускаем заново.После запуска время(вычисляется для
// нее оотложенное время)берется из преференсов
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.skillbox.ascent.R
import com.skillbox.ascent.interfaces.OnBackPress
import com.skillbox.ascent.interfaces.StateAlertInterface
import com.skillbox.ascent.networking.NetStateLiveData
import com.skillbox.ascent.ui.onboarding.OnBoardingActivity
import com.skillbox.ascent.utils.NotificationChannels
import com.skillbox.ascent.worker.OneTimeWorker.Companion.WORKER_SUCCESS_KEY
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), StateAlertInterface, OnBackPress {
    private val viewModel: MainActivityViewModel by viewModels()
    private var isObserveWM = false
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Ascent)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.readStateFirstLaunch()//проверяем , является ли запуск первым
        viewModel.readStateAlert()  //читаем настройки напоминаний из преференсов
        stateBottomNavigationView()
        bindViewModel()
        val navController = findNavController(R.id.fragment)
        bottomNavigationView.setupWithNavController(navController)
    }


    private fun bindViewModel() {
        viewModel.stateFirstLaunchApp.observe(this) { stateFirstLaunch ->
            //если запустили первые раз, то идем в репозиторий и в SharedPreferences записываем true
            if (!stateFirstLaunch) {
                viewModel.writeFlagFirstLaunchApp()
                val intent = Intent(this, OnBoardingActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private fun stateBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigationView.isVisible = false
    }


    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", " onResume")
        observeWorkManager()
        observeStateAlert()
        observeNetState()
    }

    private fun observeStateAlert() {
        viewModel.stateAlertLiveData.observe(this) {
            if (it.onOffButtonIsOn) {
                viewModel.startWM()
                isObserveWM = it.onOffButtonIsOn
            }
        }
    }


    private fun observeWorkManager() {
        if (isObserveWM) {
            WorkManager.getInstance(applicationContext)
                .getWorkInfosForUniqueWorkLiveData(ALERT_WORKER_ID).observe(this) {
                    if (it.isEmpty()) return@observe
                    handleAlertWorkInfo(it.first())
                }
        }
    }


    private fun observeNetState() {
        NetStateLiveData(applicationContext).observe(this) { isConnect ->
            isConnect?.let { viewModel.getLazyActivity() }
        }
    }

    private fun handleAlertWorkInfo(workInfo: WorkInfo) {
        val isFinish = workInfo.state.isFinished
        val isActivityAdd = workInfo.outputData.getBoolean(WORKER_SUCCESS_KEY, false)
        if (isFinish) {
            if (!isActivityAdd) { //если не добавили тренировку, то выводим напоминание
                showNotification()
                viewModel.startWM()  //если задача выполнена, запускаем заново
            }

        }
    }

    private fun showNotification() {
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_alarm)
        val notification =
            NotificationCompat.Builder(this, NotificationChannels.MESSAGE_CHANNEL_ID)
                .setContentTitle("${getString(R.string.notif_title)} ${getString(R.string.app_name)}")
                .setContentText(getString(R.string.notif_text))
                .setPriority(NotificationCompat.PRIORITY_HIGH) //чтобы показывало на всех версиях ОС
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))  //чтобы показывало на всех версиях ОС
                .setSmallIcon(R.drawable.ascent).setLargeIcon(largeIcon).build()
        NotificationManagerCompat.from(this).notify(MSG_NOTIFICATION_ID, notification)
    }

    //из фрагмента установок напоминий получаем команду на подписку WM
    override fun isStartWM(isStart: Boolean) {
        isObserveWM = if (isStart) {
            viewModel.startWM()
            true
        } else {
            viewModel.cancelWM()
            false
        }
    }

    companion object {
        const val ALERT_WORKER_ID = "alert_worker_id"
        const val TRACKING_ADD_ACTIVITY = "tracking_add_activity"
        const val FLAG_ADDED_KEY = "flag_added_key"
        private const val MSG_NOTIFICATION_ID = 2222
    }
}

