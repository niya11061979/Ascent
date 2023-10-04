package com.skillbox.ascent.ui.share.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.skillbox.ascent.R
import com.skillbox.ascent.data.contact.Contact
import com.skillbox.ascent.databinding.ItemContactBinding
import com.skillbox.ascent.utils.inflate


class ContactListAdapterDelegate(
    private val onClickContact: (contact: Contact) -> Unit
) : AbsListItemAdapterDelegate<Contact, Contact, ContactListAdapterDelegate.Holder>() {

    override fun isForViewType(item: Contact, items: MutableList<Contact>, position: Int): Boolean {
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup): Holder {
        return parent.inflate(ItemContactBinding::inflate).let { Holder(it, onClickContact) }
    }

    override fun onBindViewHolder(item: Contact, holder: Holder, payloads: MutableList<Any>) {
        holder.bind(item)
    }

    class Holder(
        private val binding: ItemContactBinding,
        onClickContact: (contact: Contact) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentContact: Contact? = null

        init {
            binding.itemContact.setOnClickListener {
                currentContact?.let(onClickContact)
            }
        }

        fun bind(contact: Contact) {
            currentContact = contact
            binding.contactNameTextView.text = contact.name
            binding.contactTelTextView.text =
                if (contact.phones.isNotEmpty()) contact.phones[0] else ""
            Glide.with(itemView)
                .load(contact.contactPhotoUri)
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_contact_placeholder)
                .into(binding.contactPhotoImageView)

        }
    }
}