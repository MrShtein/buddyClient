package mr.shtein.buddyandroidclient.utils.event

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import mr.shtein.buddyandroidclient.utils.OnSnapPositionChangeListener

class SnapOnScrollListener(
    private val snapHelper: SnapHelper,
    var onSnapPositionChangeListener: OnSnapPositionChangeListener? = null
): RecyclerView.OnScrollListener() {

    private var snapPosition = RecyclerView.NO_POSITION

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            maybeNotifySnapPositionChange(recyclerView)
    }

    private fun maybeNotifySnapPositionChange(recyclerView: RecyclerView) {
        val snapPosition = snapHelper.getPosition(recyclerView)
        val snapPositionChanged = this.snapPosition != snapPosition
        if (snapPositionChanged) {
            onSnapPositionChangeListener?.onSnapPositionChange(snapPosition)
            this.snapPosition = snapPosition
        }
    }

    private fun SnapHelper.getPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
        val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        return layoutManager.getPosition(snapView)
    }
}