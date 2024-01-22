/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.ui.categories

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.softbankrobotics.qisdktutorials.R
import com.softbankrobotics.qisdktutorials.databinding.ActivityCategoriesBinding
import com.softbankrobotics.qisdktutorials.model.data.Tutorial
import com.softbankrobotics.qisdktutorials.model.data.TutorialCategory
import com.softbankrobotics.qisdktutorials.model.data.TutorialLevel
import com.softbankrobotics.qisdktutorials.ui.bilateralswitch.OnCheckedChangeListener

/**
 * The activity showing the tutorial categories.
 */
class CategoriesActivity : RobotActivity(), CategoriesContract.View, OnTutorialClickedListener {

    private lateinit var binding: ActivityCategoriesBinding

    private lateinit var presenter: CategoriesContract.Presenter
    private lateinit var robot: CategoriesContract.Robot
    private lateinit var router: CategoriesContract.Router

    private lateinit var tutorialAdapter: TutorialAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
        setupRecyclerView()
        setupSwitch()

        val presenter = CategoriesPresenter()
        robot = CategoriesRobot(presenter)
        router = CategoriesRouter()

        presenter.bind(this)
        robot.register(this)

        presenter.loadTutorials(TutorialCategory.TALK)
        this.presenter = presenter
    }

    override fun onResume() {
        super.onResume()
        tutorialAdapter.unselectTutorials()
        tutorialAdapter.setTutorialsEnabled(true)
    }

    override fun onDestroy() {
        robot.unregister(this)
        presenter.unbind()
        super.onDestroy()
    }

    override fun showTutorials(tutorials: List<Tutorial>) {
        runOnUiThread { tutorialAdapter.updateTutorials(tutorials) }
    }

    override fun selectTutorial(tutorial: Tutorial) {
        runOnUiThread {
            tutorialAdapter.selectTutorial(tutorial)
            tutorialAdapter.setTutorialsEnabled(false)
        }
    }

    override fun goToTutorial(tutorial: Tutorial) {
        runOnUiThread { router.goToTutorial(tutorial, this@CategoriesActivity) }
    }

    override fun selectCategory(category: TutorialCategory) {
        runOnUiThread {
            when (category) {
                TutorialCategory.TALK -> binding.talkButton.isChecked = true
                TutorialCategory.MOVE -> binding.moveButton.isChecked = true
                TutorialCategory.SMART -> binding.smartButton.isChecked = true
            }
        }
    }

    override fun selectLevel(level: TutorialLevel) {
        runOnUiThread {
            when (level) {
                TutorialLevel.BASIC -> binding.levelSwitch.setChecked(false)
                TutorialLevel.ADVANCED -> binding.levelSwitch.setChecked(true)
            }
        }
    }

    override fun onTutorialClicked(tutorial: Tutorial) {
        tutorialAdapter.selectTutorial(tutorial)
        tutorialAdapter.setTutorialsEnabled(false)
        robot.stopDiscussion(tutorial)
    }

    /**
     * Configure the buttons.
     */
    private fun setupButtons() {
        binding.talkButton.setOnClickListener {
            presenter.loadTutorials(TutorialCategory.TALK)
            robot.selectTopic(TutorialCategory.TALK)
        }

        binding.moveButton.setOnClickListener {
            presenter.loadTutorials(TutorialCategory.MOVE)
            robot.selectTopic(TutorialCategory.MOVE)
        }

        binding.smartButton.setOnClickListener {
            presenter.loadTutorials(TutorialCategory.SMART)
            robot.selectTopic(TutorialCategory.SMART)
        }

        binding.closeButton.setOnClickListener { finishAffinity() }
    }

    /**
     * Configure the recycler view.
     */
    private fun setupRecyclerView() {
        tutorialAdapter = TutorialAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = tutorialAdapter

        val drawable = getDrawable(R.drawable.empty_divider_tutorials)
        if (drawable != null) {
            val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
            dividerItemDecoration.setDrawable(drawable)
            binding.recyclerView.addItemDecoration(dividerItemDecoration)
        }
    }

    /**
     * Configure the level switch.
     */
    private fun setupSwitch() {
        binding.levelSwitch.setOnCheckedChangeListener(OnCheckedChangeListener {
            if (it) {
                presenter.loadTutorials(TutorialLevel.ADVANCED)
                robot.selectLevel(TutorialLevel.ADVANCED)
            } else {
                presenter.loadTutorials(TutorialLevel.BASIC)
                robot.selectLevel(TutorialLevel.BASIC)
            }
        })
    }
}
