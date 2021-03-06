package com.example.grocerylist3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GroceryAdapter extends RecyclerView.Adapter <GroceryAdapter.GroceryViewHolder> {
    private static final String TAG = "GrocerAdaptr*<*<*<*<*<*";
    private Context mContext;
    private Cursor mCursorGrocery;
    //private Cursor mCursorMarket;
    private OnItemListener mListener;
    private int positionMarketSelected;
    private String mSelectedMarketColumnName;
    //private ArrayList <Integer> aisleArrayBeforeEdit = null;
    private boolean isToggleEditAisleChecked = false;


    public interface OnItemListener {
        /*An interface is a completely "abstract class" that is used to group related methods with empty bodies.
         * To access the interface methods, the interface must be "implemented" (kinda like inherited) by another
         *  class with the implements keyword (instead of extends). The body of the interface method is provided
         * by the "implement" class (see onCheckBox in the MainActivity class).  */

        //void onItemClick(int position);
        void onCheckBox(int position);
        void onTextViewProductName(int position);
        //void onTextViewProductName(int position, boolean hasFocus);
    }

    public void setOnItemListener(OnItemListener listener) {
        mListener = listener;
    }


    public GroceryAdapter(Cursor cursorGrocery, Context context, String selectedMarketColumnName) {
        Log.d(TAG, "GroceryAdapter() called");
        mContext = context;
        mCursorGrocery = cursorGrocery;
        mSelectedMarketColumnName = selectedMarketColumnName;
        //mCursorMarket = cursorMarket;
    }


    public static class GroceryViewHolder extends RecyclerView.ViewHolder {
        public TextView nameText;
        public EditText aisleText;
        public CheckBox checkBox;

        public GroceryViewHolder(@NonNull View itemView, final OnItemListener listener) {
            super(itemView);

            nameText = itemView.findViewById(R.id.textViewProductName);
            aisleText = itemView.findViewById(R.id.editTextAisleNumber);
            checkBox = itemView.findViewById(R.id.checkBox);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onCheckBox(position);
                        }
                    }
                }
            });

            /* aisleText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onTextViewProductName(position, hasFocus);
                        }
                    }
                }
            });  */

            nameText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onTextViewProductName(position);
                        }
                    }
                    return true;
                }
            });
        }
    }


    @NonNull
    @Override
    public GroceryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder() called");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.grocery_item, parent, false);
        return new GroceryViewHolder(view, mListener);
    }


    @Override
    public void onBindViewHolder(@NonNull GroceryViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder() called");
        if (!mCursorGrocery.moveToPosition(position)) {
            return;
        }

        String name = mCursorGrocery.getString(mCursorGrocery.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_NAME));
        String quantity = mCursorGrocery.getString(mCursorGrocery.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_TEMP_QUANTITY));
        if (quantity == null) {
            quantity = "1";
        }
        Log.d(TAG, "onBindViewHolder: the quantity is " + quantity);
        Log.d(TAG, "onBindViewHolder:  mSelectedMarketColumnName is " + mSelectedMarketColumnName + " <<<<<<<<<<<<<<<<<<<<<<<<<");
        Integer aisle = mCursorGrocery.getInt(mCursorGrocery.getColumnIndex(mSelectedMarketColumnName));
        Log.d(TAG, "onBindViewHolder: the aisle number is " + aisle);
        Integer isInTrolley = mCursorGrocery.getInt(mCursorGrocery.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_IN_TROLLEY));
        long id = mCursorGrocery.getLong(mCursorGrocery.getColumnIndex(GroceryContract.GroceryEntry._ID));

        holder.nameText.setText(quantity + "  " + name);
        holder.checkBox.setChecked(integerToBoolean(isInTrolley));
        holder.itemView.setTag(id); //An ItemView in Android can be described as a single row item in a list. It references an item from where we find the view from its layout file.

        if (aisle.equals(-1)) {
            holder.aisleText.setText("?");
        } else {
            holder.aisleText.setText(String.valueOf(aisle));
        }

        if (isToggleEditAisleChecked) {
            holder.aisleText.setTextColor(Color.RED);
            holder.aisleText.setTypeface(null, Typeface.BOLD);
            holder.aisleText.setEnabled(true);
            //holder.aisleText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            holder.aisleText.setTextColor(Color.BLACK);
            holder.aisleText.setTypeface(null, Typeface.NORMAL);
            holder.aisleText.setEnabled(false);
        }

        if (holder.checkBox.isChecked()) {
            holder.nameText.setTextColor(Color.GRAY);
            holder.aisleText.setTextColor(Color.GRAY);
        } else {
            holder.nameText.setTextColor(Color.BLACK);
            //holder.aisleText.setTextColor(Color.BLACK);
        }

    }


    @Override
    public int getItemCount() {
        //This returns the total number of rows, not the number of rows currently in the list.
        return mCursorGrocery.getCount();
    }

    public int getTickedCount() {
        int numberOfTickedItems = 0;
        boolean moveSucceeded = mCursorGrocery.moveToFirst();
        while (moveSucceeded) {
            Integer isInTrolley = mCursorGrocery.getInt(mCursorGrocery.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_IN_TROLLEY));
            if (integerToBoolean(isInTrolley)) {
                numberOfTickedItems++;
            }
            moveSucceeded = mCursorGrocery.moveToNext();
        }
        return numberOfTickedItems;
    }

    public List<String> getItemArrayFromGroceryTable(SQLiteDatabase database) {
        List<String> itemArray = new ArrayList<String>();
        Cursor cursorAllGroceryItems = database.query(
                GroceryContract.GroceryEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                GroceryContract.GroceryEntry.COLUMN_NAME + " ASC"
        );
        boolean moveSucceeded = cursorAllGroceryItems.moveToFirst();
        while (moveSucceeded) {
            String itemName = cursorAllGroceryItems.getString(cursorAllGroceryItems.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_NAME));
            itemArray.add(itemName);
            moveSucceeded = cursorAllGroceryItems.moveToNext();
        }
        cursorAllGroceryItems.close();
        return itemArray;
    }

    public void alternateIsToggleEditAisleCheckedValue() {
        isToggleEditAisleChecked = !isToggleEditAisleChecked;
        notifyDataSetChanged();
    }


    private boolean integerToBoolean(Integer number) {
        if (number == 0) {
            return false;
        } else {
            return true;
        }
    }


    public Integer getItemPosition(String itemName) {
        Integer position = 0;
        //since we know we just added this item to the list, moveToNext() will never return null
        for (mCursorGrocery.moveToFirst();
             !itemName.equals(mCursorGrocery.getString(mCursorGrocery.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_NAME)));
             mCursorGrocery.moveToNext()) {
            position++;
        }
        Log.d(TAG, "Inside NewItemPosition, position is: " + position);
        return position;
    }

    public String getItemName(int position) {
        mCursorGrocery.moveToPosition(position); //first position is 0 (zero).
        String nameOfItemChecked = mCursorGrocery.getString(mCursorGrocery.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_NAME));
        Log.d(TAG, "Inside addItemToTrolley(), product checked is: " + nameOfItemChecked);
        return nameOfItemChecked;
    }


    public Integer getInTrolleyValue(int position) {
        boolean moveSucceeded = mCursorGrocery.moveToPosition(position);
        if (!moveSucceeded) {
            return -1;
        }
        Integer isInTrolley = mCursorGrocery.getInt(mCursorGrocery.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_IN_TROLLEY));
        Log.d(TAG, "Inside getInTrolleyValue(), WAS product in trolley? " + isInTrolley);
        return isInTrolley;
    }


    public Integer getAisleFromName(String productNameStr) {
        boolean moveSucceeded = mCursorGrocery.moveToFirst();
        while (moveSucceeded) {
            String ithItemName = mCursorGrocery.getString(mCursorGrocery.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_NAME));
            if (ithItemName.equals(productNameStr)) {
                return mCursorGrocery.getInt(mCursorGrocery.getColumnIndex(mSelectedMarketColumnName));
            }
            moveSucceeded = mCursorGrocery.moveToNext();
        }
        return -1; //This code should not be reached.
    }


    public List<Market> getMarketsList(SQLiteDatabase database) {
        Log.d(TAG, "getMarketsList: ");
        Cursor cursorMarkets = getAllSupermarkets(database);
        List<Market> newSpinnerArray = new ArrayList<Market>();
        //iterate through all rows and append the name/location and ID to Market list.
        boolean moveSucceeded = cursorMarkets.moveToFirst();
        int position = 0;
        while (moveSucceeded) {
            Log.d(TAG, "getMarketsList:       " + position + "th iteration of while loop.");
            String marketName = cursorMarkets.getString(cursorMarkets.getColumnIndex(GroceryContract.SupermarketsVisited.COLUMN_MARKET_NAME));
            String marketLocation = cursorMarkets.getString(cursorMarkets.getColumnIndex(GroceryContract.SupermarketsVisited.COLUMN_MARKET_LOCATION));
            Integer marketID = cursorMarkets.getInt(cursorMarkets.getColumnIndex(GroceryContract.SupermarketsVisited._ID));
            boolean marketIsSelected = integerToBoolean(cursorMarkets.getInt(cursorMarkets.getColumnIndex(GroceryContract.SupermarketsVisited.COLUMN_IS_MARKET_SELECTED)));
            Market market = new Market(marketName, marketLocation, marketID, marketIsSelected);
            newSpinnerArray.add(market);
            Log.d(TAG, "getMarketsList: not-deleted marketName: " + marketName);
            if (marketIsSelected) {
                positionMarketSelected = position;
                Log.d(TAG, "getMarketsList:  market" + marketName + " is selected and has position: " + positionMarketSelected);
            }
            position++;
            moveSucceeded = cursorMarkets.moveToNext();
        }
        Log.d(TAG, "getMarketsList:  F I N I S H E D !!!");
        cursorMarkets.close();
        return newSpinnerArray;
    }


    public int getPositionMarketSelected() {
        Log.d(TAG, "getPositionMarketSelected:   position of marketSelected is " + positionMarketSelected);
        return positionMarketSelected;
    }


    public void setPositionMarketSelected(int position) {
        positionMarketSelected = position;
    }


    public void setmSelectedMarketColumnName(String selectedMarketColumnName) {
        Log.d(TAG, "setmSelectedMarketColumnName:  new value of mSelectedMarketColumnName is " + this.mSelectedMarketColumnName);
        this.mSelectedMarketColumnName = selectedMarketColumnName;
    }


    static public String getSelectedMarketGroceryListColumnName(SQLiteDatabase database) {
        Integer SQL_TRUE = 1;
        String[] selectionArgs = {String.valueOf(SQL_TRUE)};
        Cursor cursorMarkets = database.query(
                GroceryContract.SupermarketsVisited.TABLE_NAME_MARKET,
                null,
                GroceryContract.SupermarketsVisited.COLUMN_IS_MARKET_SELECTED + " =?",
                selectionArgs,
                null,
                null,
                null
        );

        String selectedMarketGroceryListColumnName;
        boolean moveSucceeded = cursorMarkets.moveToFirst();
        if (!moveSucceeded) {
            selectedMarketGroceryListColumnName = "market1AisleLocation";
        } else {
            String marketName = cursorMarkets.getString(cursorMarkets.getColumnIndex(GroceryContract.SupermarketsVisited.COLUMN_MARKET_NAME));
            long relativeID = cursorMarkets.getLong(cursorMarkets.getColumnIndex(GroceryContract.SupermarketsVisited.COLUMN_MARKET_GROCERY_COLUMN));
            Log.d(TAG, "getSelectedMarketGroceryListColumnNumber: marketName is: " + marketName + "and its relativeID is: " + relativeID);
            String selectedMarketColumnName = "market" + relativeID + "AisleLocation";
            cursorMarkets.close();
            return selectedMarketColumnName;
        }
        cursorMarkets.close();
        return selectedMarketGroceryListColumnName;
    }


    static public boolean marketTableIsNotEmpty(SQLiteDatabase database) {
        Cursor cursorMarkets = database.query(
                GroceryContract.SupermarketsVisited.TABLE_NAME_MARKET,
                null,
                null,
                null,
                null,
                null,
                null
        );
        boolean moveSucceeded = cursorMarkets.moveToFirst();
        cursorMarkets.close();
        return moveSucceeded;
    }


    static public boolean marketIsAlreadyInTable(SQLiteDatabase database, String newMarketName, String newMarketLocation) {
        String[] selectionArgs = {newMarketName, newMarketLocation};
        Cursor cursorMarkets = database.query(
                GroceryContract.SupermarketsVisited.TABLE_NAME_MARKET,
                null,
                GroceryContract.SupermarketsVisited.COLUMN_MARKET_NAME + " =? AND " +
                        GroceryContract.SupermarketsVisited.COLUMN_MARKET_LOCATION + " =?",
                selectionArgs,
                null,
                null,
                null
        );
        boolean moveSucceeded = cursorMarkets.moveToFirst();
        cursorMarkets.close();
        return moveSucceeded;
    }


    static public int getNewestGroceryListColumnNumber(SQLiteDatabase database) {
        Cursor cursorMarkets = database.query(
                GroceryContract.SupermarketsVisited.TABLE_NAME_MARKET,
                null,
                null,
                null,
                null,
                null,
                GroceryContract.SupermarketsVisited.COLUMN_MARKET_GROCERY_COLUMN + " ASC"
        );

        int newestGroceryListColumnNumber;
        boolean moveSucceeded = cursorMarkets.moveToLast();
        if (!moveSucceeded) {
            Log.d(TAG, "getNewestGroceryListColumnNumber:  move did not succeed because table is empty");
            newestGroceryListColumnNumber = 1;
        } else {
            String marketName = cursorMarkets.getString(cursorMarkets.getColumnIndex(GroceryContract.SupermarketsVisited.COLUMN_MARKET_NAME));
            String marketLocation = cursorMarkets.getString(cursorMarkets.getColumnIndex(GroceryContract.SupermarketsVisited.COLUMN_MARKET_LOCATION));
            newestGroceryListColumnNumber = cursorMarkets.getInt(cursorMarkets.getColumnIndex(GroceryContract.SupermarketsVisited.COLUMN_MARKET_GROCERY_COLUMN)) + 1;
            Log.d(TAG, "getNewestGroceryListColumnNumber: " + marketName + " (" + marketLocation + ") - newHighestRelativeID: " + newestGroceryListColumnNumber);
        }
        cursorMarkets.close();
        return newestGroceryListColumnNumber;
    }


    // this is possibly failing to close
    private Cursor getAllSupermarkets(SQLiteDatabase database) {
        Log.d(TAG, "getAllSupermarkets() called");
        return database.query(
                GroceryContract.SupermarketsVisited.TABLE_NAME_MARKET,
                null,
                null,
                null,
                null,
                null,
                GroceryContract.SupermarketsVisited.COLUMN_MARKET_NAME + " ASC, " + GroceryContract.SupermarketsVisited.COLUMN_MARKET_LOCATION + " ASC"
        );
    }


    public void closeAllCursors() {
//        if (mCursorMarket != null) {
//            mCursorMarket.close(); //close and get rid of cursor
//        }
        if (mCursorGrocery != null) {
            mCursorGrocery.close(); //close and get rid of cursor
        }
    }


    public boolean areAllCursorsClosed() {
        return mCursorGrocery.isClosed(); // && mCursorMarket.isClosed();
    }


    //every time we update database, we have to pass a new cursor..
    public void swapCursorGrocery(Cursor newCursor) {
        if (mCursorGrocery != null) {
            mCursorGrocery.close(); //close and get rid of cursor
        }

        mCursorGrocery = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged(); //update recyclerview
        }
    }

}