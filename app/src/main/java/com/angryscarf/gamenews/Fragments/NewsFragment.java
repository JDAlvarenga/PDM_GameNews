package com.angryscarf.gamenews.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.angryscarf.gamenews.Fragments.Adapters.NewsAdapter;
import com.angryscarf.gamenews.Model.Data.New;
import com.angryscarf.gamenews.Model.GameNewsViewModel;
import com.angryscarf.gamenews.R;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsFragment.OnNewsFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment implements NewsAdapter.onNewsAdapterInteractionListener{

    private OnNewsFragmentInteractionListener mListener;
    private RecyclerView recycler;
    private NewsAdapter adapter;
    private GameNewsViewModel viewModel;

    public NewsFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewsFragment.
     */

    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(GameNewsViewModel.class);
        adapter = new NewsAdapter(this, null);
        viewModel.getAllnews()
                .subscribe(news -> {
            adapter.setDataSet(news);
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_news, container, false);

        recycler = v.findViewById(R.id.news_recycler);
        recycler.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(this.getContext(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

            @Override
            public int getSpanSize(int position) {
                if(position % 3 == 0) {
                    return 2;
                }
                else {
                    return 1;
                }
            }
        });

        recycler.setLayoutManager(manager);

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNewsFragmentInteractionListener) {
            mListener = (OnNewsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNewsFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onFavoriteSelected(New aNew) {
        viewModel.toggleFavoriteNew(aNew);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnNewsFragmentInteractionListener {

    }
}
