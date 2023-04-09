package com.codinginflow.mvvmtodo.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.databinding.ItemTaskBinding

// use ListAdapter when you have reactive data source, because it always recieves a completely new list
// and updates the changes from the old and new list
class TasksAdapter(private val listener: OnItemClickListener) : ListAdapter<Task, TasksAdapter.TasksViewHolder>(DiffCallback()) {

    // creates new item in list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        // context is the fragment/activity where recycler view is, inflates the binding layout
        // inflation means xml file becomes an object
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TasksViewHolder(binding)
    }

    // binds the item at position using bind fun logic
    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    // knows where the views in our layouts are utilising viewbinding
    // nested class for TasksAdapter
    inner class TasksViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        init { // init block only called when viewholder is instantiated
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    // incase click item that is animating and deleting but not valid anymore
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        listener.onItemClick(task)
                    }
                }
                checkBoxCompleted.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        listener.onCheckBoxClick(task, checkBoxCompleted.isChecked)
                    }
                }
            }
        }

        // puts data into the views inside the layout
        fun bind(task: Task) {
            binding.apply {
                checkBoxCompleted.isChecked = task.completed
                textViewName.text = task.name
                textViewName.paint.isStrikeThruText = task.completed
                labelPriority.isVisible = task.important
            }
        }
    }

    // use interface to "decouple" fragment from adapter, so that the adapter remains reusable
    interface OnItemClickListener {
        fun onItemClick(task: Task)
        fun onCheckBoxClick(task: Task, isChecked: Boolean)
    }

    // define how the ListAdapter can detect changes between old and new list
    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        // item changes position in list without changing contents
        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
            // id is unique for each item, determines if same
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
            oldItem == newItem
        /*
        // when contents within an item have changed, refresh item on screen
        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            // compares all properties of Task class
            return oldItem == newItem
        }
        */
    }

}