package xyz.khash.flexboxradiogroup

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.annotation.IdRes
import androidx.appcompat.widget.AppCompatRadioButton
import com.google.android.flexbox.FlexboxLayout


/**
 * Created by Khash Mortazavi on 2020-09-10
 *
 * A radio group that extends from FlexboxLayout, allowing it to automatically adjust the children to go
 * to the next line if necessary. Has all the functionality of RadioGroup
 *
 * Children are to be of [AppCompatRadioButton]
 *
 * In order to add spacing between items and between vertical lines, set the horizontal/vertical
 * dividers to true (showDividerVertical and showDividerHorizontal) and set the divider drawable
 * (dividerDrawableVertical and dividerDrawableHorizontal).
 */
class FlexboxRadioGroup : FlexboxLayout {
    /**
     * Returns the identifier of the selected radio button in this group.
     * Upon empty selection, the returned value is -1.
     *
     * @return the unique id of the selected radio button in this group
     * @attr ref android.R.styleable#RadioGroup_checkedButton
     * @see .check
     * @see .clearCheck
     */
    // holds the checked id; the selection is empty by default

    var checkedRadioButtonId = -1

    // tracks children radio buttons checked state
    private var childOnCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null

    // when true, mOnCheckedChangeListener discards events
    private var protectFromCheckedChange = false
    private var onCheckedChangeListener: OnCheckedChangeListener? = null
    private var passThroughListener: PassThroughHierarchyChangeListener? = null

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    init {
        childOnCheckedChangeListener = CheckedStateTracker()
        passThroughListener = PassThroughHierarchyChangeListener()
        super.setOnHierarchyChangeListener(passThroughListener)
    }

    override fun setOnHierarchyChangeListener(listener: OnHierarchyChangeListener) {
        // the user listener is delegated to our pass-through listener
        passThroughListener?.mOnHierarchyChangeListener = listener
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        // checks the appropriate radio button as requested in the XML file
        if (checkedRadioButtonId != -1) {
            protectFromCheckedChange = true
            setCheckedStateForView(checkedRadioButtonId, true)
            protectFromCheckedChange = false
            setCheckedId(checkedRadioButtonId)
        }
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
        if (child is AppCompatRadioButton) {
            if (child.isChecked) {
                protectFromCheckedChange = true
                if (checkedRadioButtonId != -1) {
                    setCheckedStateForView(checkedRadioButtonId, false)
                }
                protectFromCheckedChange = false
                setCheckedId(child.id)
            }
        }
        super.addView(child, index, params)
    }

    /**
     * Sets the selection to the radio button whose identifier is passed in
     * parameter. Using -1 as the selection identifier clears the selection;
     * such an operation is equivalent to invoking [.clearCheck].
     *
     * @param id the unique id of the radio button to select in this group
     * @see .getCheckedRadioButtonId
     * @see .clearCheck
     */
    fun check(@IdRes id: Int) {
        // don't even bother
        if (id != -1 && id == checkedRadioButtonId) {
            return
        }
        if (checkedRadioButtonId != -1) {
            setCheckedStateForView(checkedRadioButtonId, false)
        }
        if (id != -1) {
            setCheckedStateForView(id, true)
        }
        setCheckedId(id)
    }

    /**
     * Method for finding and retrieving child [AppCompatRadioButton]
     *
     * @param id: id of the child
     * @return the child view [AppCompatRadioButton] if one exists, null otherwise
     */
    fun getChildRadioButtonById(id: Int): AppCompatRadioButton? {
        if (this.childCount < 1) return null
        for (i in 0 until this.childCount) {
            val child = getChildAt(i) as AppCompatRadioButton
            if (child.id == id) return child
        }
        return null
    }

    /**
     * Searches the children using its id
     *
     * @param id: if of the child view
     * @return: true if such a view exists, false otherwise
     */
    operator fun contains(id: Int): Boolean {
        if (this.childCount < 1) return false
        for (i in 0 until this.childCount) {
            val child = getChildAt(i) as AppCompatRadioButton
            if (child.id == id) return true
        }
        return false
    }

    /**
     * Reset the group: clears all checks and set the first child (index 0) to checked (if it is
     * not empty)
     */
    fun resetGroup() {
        clearCheck()
        if (this.childCount > 1) (getChildAt(0) as AppCompatRadioButton).isChecked = true
    }

    private fun setCheckedId(@IdRes id: Int) {
        checkedRadioButtonId = id
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener!!.onCheckedChanged(this, checkedRadioButtonId)
        }
    }

    private fun setCheckedStateForView(viewId: Int, checked: Boolean) {
        val checkedView: View? = findViewById(viewId)
        if (checkedView != null && checkedView is AppCompatRadioButton) {
            checkedView.isChecked = checked
        }
    }

    /**
     * Clears the selection. When the selection is cleared, no radio button
     * in this group is selected and [.getCheckedRadioButtonId] returns
     * null.
     *
     * @see .check
     * @see .getCheckedRadioButtonId
     */
    fun clearCheck() {
        check(-1)
    }

    /**
     * Register a callback to be invoked when the checked radio button
     * changes in this group.
     *
     * @param listener the callback to call on checked state change
     */
    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener?) {
        onCheckedChangeListener = listener
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    override fun getAccessibilityClassName(): CharSequence {
        return RadioGroup::class.java.name
    }

    interface OnCheckedChangeListener {
        /**
         * Called when the checked radio button has changed. When the
         * selection is cleared, checkedId is -1.
         *
         * @param group     the group in which the checked radio button has changed
         * @param checkedId the unique identifier of the newly checked radio button
         */
        fun onCheckedChanged(group: FlexboxRadioGroup?, @IdRes checkedId: Int)
    }

    private inner class CheckedStateTracker : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            // prevents from infinite recursion
            if (protectFromCheckedChange) {
                return
            }
            protectFromCheckedChange = true
            if (checkedRadioButtonId != -1) {
                setCheckedStateForView(checkedRadioButtonId, false)
            }
            protectFromCheckedChange = false
            val id = buttonView.id
            setCheckedId(id)
        }
    }

    private inner class PassThroughHierarchyChangeListener :
        OnHierarchyChangeListener {
        var mOnHierarchyChangeListener: OnHierarchyChangeListener? = null
        override fun onChildViewAdded(parent: View, child: View) {
            if (parent === this@FlexboxRadioGroup && child is AppCompatRadioButton) {
                var id: Int = child.getId()
                // generates an id if it's missing
                if (id == View.NO_ID) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        id = View.generateViewId()
                    }
                    child.setId(id)
                }
                child.setOnCheckedChangeListener(
                    childOnCheckedChangeListener
                )
            }
            mOnHierarchyChangeListener?.onChildViewAdded(parent, child)
        }

        override fun onChildViewRemoved(parent: View, child: View?) {
            if (parent === this@FlexboxRadioGroup && child is AppCompatRadioButton) {
                child.setOnCheckedChangeListener(null)
            }
            mOnHierarchyChangeListener?.onChildViewRemoved(parent, child)
        }
    }
}