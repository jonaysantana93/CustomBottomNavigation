package com.jonay.custombottomnavigation.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MenuInflater
import android.widget.LinearLayout
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import com.jonay.custombottomnavigation.R
import com.jonay.custombottomnavigation.databinding.CustomBottomNavigationBinding
import com.jonay.custombottomnavigation.databinding.CustomBottomNavigationItemBinding

/***
 * Created by Jonay Adrián Santana González on 29/08/2025.
 * All rights reserved 2025
 ***/

@SuppressLint("ViewConstructor")
class CustomBottomNavigationView @JvmOverloads constructor(
    private val context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: CustomBottomNavigationBinding = CustomBottomNavigationBinding.inflate(
        LayoutInflater.from(context), this, true)
    private lateinit var listener: OnItemSelectedListener
    private val itemBindingMap: MutableMap<Int, CustomBottomNavigationItemBinding> = mutableMapOf()
    private var selectedItemID: Int = -1

    private var indicatorColor: Int = R.color.blue
    private var colorItemSelected: Int = R.color.blue
    private var colorItemUnselected: Int = R.color.black

    init {
        attrs?.let {
            val array: TypedArray = context.obtainStyledAttributes(it, R.styleable.CustomBottomNavigationView)
            try {
                buildMenuOptions(array.getResourceId(R.styleable.CustomBottomNavigationView_menu, 0))
                this.indicatorColor = array.getResourceId(R.styleable.CustomBottomNavigationView_colorItemSelector, R.color.blue)
                this.colorItemSelected = array.getResourceId(R.styleable.CustomBottomNavigationView_colorItemSelected, R.color.blue)
                this.colorItemUnselected = array.getResourceId(R.styleable.CustomBottomNavigationView_colorItemUnselected, R.color.black)
            } finally {
                array.recycle()
            }
        }
    }

    fun setOnItemSelectedListener(listener: OnItemSelectedListener) {
        this.listener = listener
    }

    fun setSelectedItem(itemID: Int) {
        if (::listener.isInitialized) {
            setSelectItem(itemID)
            listener.onItemSelected(itemID)
        }
    }

    fun getSelectedItem(): Int = selectedItemID

    @SuppressLint("RestrictedApi")
    private fun buildMenuOptions(menuId: Int) {
        if (menuId != 0) {
            val menu = MenuBuilder(context)
            val inflater: MenuInflater = MenuInflater(context)
            inflater.inflate(menuId, menu)

            menu.forEach { item ->
                val itemID = item.itemId
                val itemTitle = item.title
                val itemIcon = item.icon

                val view = buildBottomNavigationItem(itemID, itemTitle, itemIcon)
                itemBindingMap.put(itemID, view)
                binding.listOfItems.addView(view.root)

                if (selectedItemID != -1) {
                    setSelectItem(itemID)
                }
            }
        }
    }

    private fun buildBottomNavigationItem(itemID: Int, itemTitle: CharSequence?, itemIcon: Drawable?): CustomBottomNavigationItemBinding =
        CustomBottomNavigationItemBinding.inflate(LayoutInflater.from(context)).apply {
            root.apply {
                id = itemID
                setOnClickListener {
                    if (::listener.isInitialized) {
                        setSelectItem(itemID)
                        listener.onItemSelected(itemID)
                    }
                }

                layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
            }

            this.itemIcon.setImageDrawable(itemIcon)
            this.itemTitle.text = itemTitle
        }

    private fun setSelectItem(itemID: Int) {
        if (itemID == selectedItemID) return

        if (selectedItemID != -1) {
            itemBindingMap[selectedItemID]?.apply {
                selectedItemIndicator.visibility = INVISIBLE
                itemTitle.setTextColor(ContextCompat.getColor(context, colorItemUnselected))
                itemIcon.setColorFilter(ContextCompat.getColor(context, colorItemUnselected))
            }
        }

        itemBindingMap[itemID]?.apply {
            selectedItemIndicator.visibility = VISIBLE
            selectedItemIndicator.setBackgroundColor(ContextCompat.getColor(context, indicatorColor))
            itemTitle.setTextColor(ContextCompat.getColor(context, colorItemSelected))
            itemIcon.setColorFilter(ContextCompat.getColor(context, colorItemSelected))
        }

        selectedItemID = itemID
    }
}