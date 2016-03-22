package com.dionlan.uaibuy.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.dionlan.uaibuy.R;

import java.util.ArrayList;
import java.util.List;

public class ContatosFragment extends Fragment {

    ArrayList<String> usuarios;
    ArrayAdapter arrayAdapter;
    View view = null;

    public ContatosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.i("AppInfo", "CONTATOS FRAGMENT");

        view = inflater.inflate(R.layout.fragment_contatos, container, false);

        if (ParseUser.getCurrentUser().get("seguindo") == null) {

            List<String> listaVazia = new ArrayList<String>();
            ParseUser.getCurrentUser().put("seguindo", listaVazia);

        }

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);


        usuarios = new ArrayList<String>();

        arrayAdapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_checked, usuarios);

        final ListView listView = (ListView) view.findViewById(R.id.listaUsuarios);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(arrayAdapter);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {

                    usuarios.clear();

                    for (ParseUser user : objects) {
                        usuarios.add(user.getUsername());
                    }

                    arrayAdapter.notifyDataSetChanged();

                    for (String username : usuarios) {

                        if (ParseUser.getCurrentUser().getList("seguindo").contains(username)) {

                            listView.setItemChecked(usuarios.indexOf(username), true);
                        }
                    }
                } else {

                    e.printStackTrace();

                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CheckedTextView checkedTextView = (CheckedTextView) view;
                if (checkedTextView.isChecked()) {

                    Log.i("AppInfo", "Linha is checked");

                    ParseUser.getCurrentUser().getList("seguindo").add(usuarios.get(position));
                    ParseUser.getCurrentUser().saveInBackground();

                } else {

                    Log.i("AppInfo", "Linha is not checked");

                    ParseUser.getCurrentUser().getList("seguindo").remove(usuarios.get(position));
                    ParseUser.getCurrentUser().saveInBackground();

                }

            }

        });
        return view;
    }
}