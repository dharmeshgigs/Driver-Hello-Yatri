package com.helloyatri.utils.extension

import androidx.recyclerview.widget.RecyclerView


fun RecyclerView.Adapter<*>.notifyItemRangeInserted(
    mainList: ArrayList<*>,
    listToBeAdded: ArrayList<*>
) {
    notifyItemRangeInserted(mainList.size - listToBeAdded.size, mainList.size)
}


fun RecyclerView.Adapter<*>.notifyItemRangeChanged(
    mainList: ArrayList<*>,
    listToBeChanged: ArrayList<*>
) {
    notifyItemRangeChanged(mainList.size - listToBeChanged.size, mainList.size)
}

fun RecyclerView.Adapter<*>.notifyItemRangeRemoved(
    mainList: ArrayList<*>,
    listToBeRemoved: ArrayList<*>
) {
    notifyItemRangeRemoved(mainList.size - listToBeRemoved.size, mainList.size)
}
