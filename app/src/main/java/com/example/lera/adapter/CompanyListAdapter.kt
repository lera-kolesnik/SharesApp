package com.example.lera.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.lera.R
import com.example.lera.data.model.Company
import com.example.lera.data.repo.CompanyRepository.Companion.AAPL
import com.example.lera.data.repo.CompanyRepository.Companion.AMZN
import com.example.lera.data.repo.CompanyRepository.Companion.GOOGL
import com.example.lera.data.repo.CompanyRepository.Companion.MSFT
import com.example.lera.data.repo.CompanyRepository.Companion.TSLA
import com.example.lera.data.repo.CompanyRepository.Companion.YNDX
import com.squareup.picasso.Picasso

/**
 * Adapter with logic to render stock views correctly.
 * The colour of item background depends on the order.
 * Adapter is updated whenever we receive new data changes.
 */
class CompanyListAdapter(
    private val context: Context,
    private var companies: List<Company> = listOf()
): RecyclerView.Adapter<CompanyListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutResource = if (viewType == DARK_LAYOUT) {
            R.layout.company_dark_layout
        } else {
            R.layout.company_layout
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutResource, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = companies.size

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            LIGHT_LAYOUT
        } else {
            DARK_LAYOUT
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val companySymbolTv: TextView = holder.itemView.findViewById(R.id.companySymbolTv)
        val companyNameTv: TextView = holder.itemView.findViewById(R.id.companyNameTv)
        val companyIcon: ImageView = holder.itemView.findViewById(R.id.companyIcon)
        val companyPrice: TextView = holder.itemView.findViewById(R.id.companyPrice)
        val companyPriceChange: TextView = holder.itemView.findViewById(R.id.companyPriceChange)

        val company = companies[position]
        companySymbolTv.text = company.symbol
        companyNameTv.text = company.name
        company.price?.let { companyPrice.text = "$$it" }
        val priceChange = company.priceChange
        val changePercent = company.changePercent
        if (priceChange != null && changePercent != null) {
            val isPositive = priceChange > 0
            val isNeural = priceChange == 0.0
            val isNegative = !isPositive and !isNeural
            companyPriceChange.setTextColor(
                ContextCompat.getColor(
                    context,
                    when {
                        isPositive -> R.color.green
                        isNeural -> R.color.grey
                        else -> R.color.red
                    }
                )
            )
            val sign = if (isNeural || isNegative) "" else "+"
            companyPriceChange.text = "$sign$priceChange ($changePercent%)"
        }
        val iconResId = when (company.symbol) {
            TSLA -> R.drawable.ic_tsla
            AMZN -> R.drawable.ic_amzn
            AAPL -> R.drawable.ic_aapl
            GOOGL -> R.drawable.ic_googl
            MSFT -> R.drawable.ic_msft
            YNDX -> R.drawable.ic_yndx
            else -> null
        }
        iconResId?.let {
            companyIcon.setImageResource(it)
        } ?: Picasso.get()
                .load(company.url)
                .placeholder(R.drawable.ic_search)
                .resize(50, 50)
                .into(companyIcon)
    }

    fun updateList(list: List<Company>){
        companies = list
    }

    fun get(position: Int): Company = companies[position]

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    companion object {
        private const val DARK_LAYOUT = 0
        private const val LIGHT_LAYOUT = 1
    }
}

