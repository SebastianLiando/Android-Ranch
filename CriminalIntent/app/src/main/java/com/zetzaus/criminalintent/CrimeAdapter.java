package com.zetzaus.criminalintent;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This class is a subclass of the <code>RecyclerView.Adapter</code> class. It is used as the adapter of the
 * lists of crimes in <code>CrimeListActivity</code>.
 *
 * @see CrimeListActivity
 */
public class CrimeAdapter extends RecyclerView.Adapter<CrimeAdapter.ViewHolder> {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_SERIOUS = 1;

    private List<Crime> mCrimes;
    private int mLastClickPos;

    /**
     * Constructs a <code>CrimeAdapter</code> by initializing the list of crimes.
     *
     * @param crimes the list of crimes.
     */
    public CrimeAdapter(List<Crime> crimes) {
        mCrimes = crimes;
    }

    /**
     * Creates a new <code>CrimeAdapter.ViewHolder</code> depending on the seriousness of the crime.
     *
     * @param parent   the parent layout.
     * @param viewType the type of the crime.
     * @return the new <code>CrimeAdapter.ViewHolder</code>.
     */
    @NonNull
    @Override
    public CrimeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        int layoutId = R.layout.item_crime;
        if (viewType == TYPE_SERIOUS) layoutId = R.layout.item_crime_serious;
        return new ViewHolder(inflater.inflate(layoutId, parent, false));
    }

    /**
     * Binds the data of a crime to a row in the <code>RecyclerView</code>.
     *
     * @param holder   the <code>ViewHolder</code> containing the views.
     * @param position the current position of the data.
     */
    @Override
    public void onBindViewHolder(@NonNull CrimeAdapter.ViewHolder holder, int position) {
        Crime crimeToBind = mCrimes.get(position);
        holder.bind(crimeToBind);
    }

    /**
     * Returns the number of crimes in the list.
     *
     * @return the number of crimes in the list.
     */
    @Override
    public int getItemCount() {
        return mCrimes.size();
    }

    /**
     * Returns the type of item depending whether the crime requires police.
     *
     * @param position the position of the item.
     * @return the type of item.
     */
    @Override
    public int getItemViewType(int position) {
        Crime crime = mCrimes.get(position);

        if (crime.isSolved()) return TYPE_NORMAL;
        if (crime.isRequiresPolice()) return TYPE_SERIOUS;
        return TYPE_NORMAL;
    }

    /**
     * Updates the list at the row where the data changed.
     */
    public void updateList() {
        notifyItemChanged(mLastClickPos);
    }

    /**
     * This class holds the layout of one item of the <code>RecyclerView</code>.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextTitle;
        private TextView mTextDate;
        private ImageView mImageSolved;

        private Crime mCrime;

        /**
         * Constructs a <code>ViewHolder</code> object.
         *
         * @param view the view to hold.
         */
        public ViewHolder(View view) {
            super(view);

            mTextTitle = view.findViewById(R.id.text_crime_title);
            mTextDate = view.findViewById(R.id.text_crime_date);
            mImageSolved = view.findViewById(R.id.image_crime_solved);
        }

        /**
         * Binds crime information wrapped in a <code>Crime</code> object to the text views.
         *
         * @param crime the <code>Crime</code> object to be bound.
         */
        public void bind(Crime crime) {
            mCrime = crime;
            mTextTitle.setText(crime.getTitle());
            mTextDate.setText(crime.getDateString());
            itemView.setOnClickListener(this);
            if (!crime.isSolved()) {
                mImageSolved.setVisibility(View.INVISIBLE);
            } else {
                mImageSolved.setVisibility(View.VISIBLE);
            }
        }

        /**
         * Starts <code>CrimeActivity</code> on click.
         *
         * @param v the clicked view.
         */
        @Override
        public void onClick(View v) {
            mLastClickPos = getBindingAdapterPosition();
            Intent intent = CrimeActivity.newIntent(v.getContext(), mCrime.getId());
            v.getContext().startActivity(intent);
        }
    }
}
