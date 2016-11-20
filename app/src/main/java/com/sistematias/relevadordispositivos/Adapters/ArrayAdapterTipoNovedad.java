package com.sistematias.relevadordispositivos.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sistematias.relevadordispositivos.R;
import com.sistematias.relevadordispositivos.model.TipoNovedad;

import java.util.ArrayList;

/**
 * Created by samuel on 26/10/2015.
 */
public class ArrayAdapterTipoNovedad extends BaseAdapter {
    private Context contexto;
    private ArrayList<TipoNovedad> data;
    public ArrayAdapterTipoNovedad(Context context,ArrayList<TipoNovedad> objects) {
        contexto = context;
        data = objects;
    }
    /**
     * How many items are in the data set represented by this Adapter.
     Returns
     Count of items.

     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.

     Parameters
     position  Position of the item whose data we want within the adapter's data set.

     Returns
     The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.

     Parameters
     position  The position of the item within the adapter's data set whose row id we want.

     Returns
     The id of the item at the specified position.

     */
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TipoNovedad tipoNovedad = (TipoNovedad)getItem(position);
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.customsp, null);
        }
        TextView tfItem = (TextView) row.findViewById(R.id.itemvalue);
        tfItem.setText(tipoNovedad.getDescripcion());
        return row;
    }
}



