package com.zj.album

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.zj.album.launcher.LauncherFragment
import com.zj.album.nutils.AlbumOptions
import com.zj.album.nutils.Constance

@Suppress("unused")
object AlbumIns {

    fun with(act: FragmentActivity): AlbumOptions {
        return startWith(act.supportFragmentManager)
    }

    fun with(frg: Fragment): AlbumOptions {
        return startWith(frg.childFragmentManager)
    }

    private fun startWith(manager: FragmentManager): AlbumOptions {
        return AlbumOptions { it, call ->
            val bundle = Bundle()
            bundle.putSerializable(Constance.I_OPTIONS_INFO, it)
            val launch = LauncherFragment.launch(0, bundle, call)
            manager.beginTransaction().add(launch, Constance.LAUNCH_KEY).commitNow()
        }
    }
}