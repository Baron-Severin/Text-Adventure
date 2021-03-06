package com.rudie.severin.machosquad.FragmentClasses;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rudie.severin.machosquad.Activities.PlayActivity;
import com.rudie.severin.machosquad.Adapters.InventoryAdapter;
import com.rudie.severin.machosquad.InformationHolders.CurrentInventoryAndStats;
import com.rudie.severin.machosquad.InformationHolders.ItemData;
import com.rudie.severin.machosquad.InformationHolders.PH;
import com.rudie.severin.machosquad.R;

import java.util.List;

public class InventoryFragment extends android.support.v4.app.Fragment {
  Context mContext;
  RecyclerView recyclerView;

  public static InventoryFragment newInstance(int characterId) {
    InventoryFragment inventoryFragment = new InventoryFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(PH.tbl_character_id, characterId);
    inventoryFragment.setArguments(bundle);
    return inventoryFragment;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    mContext = context;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_inventory, container, false);

    recyclerView = (RecyclerView) view.findViewById(R.id.recyclerviewInventory);

    List<ItemData> inventory = CurrentInventoryAndStats.getCurrentInventory();

    InventoryAdapter adapter = new InventoryAdapter(getActivity(), inventory);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }

  public void refreshInventoryFragment() {
    CurrentInventoryAndStats.refreshFromDb(PlayActivity.getCurrentCharacterId(), getActivity());
    List<ItemData> inventory = CurrentInventoryAndStats.getCurrentInventory();
    InventoryAdapter adapter = new InventoryAdapter(getActivity(), inventory);
    recyclerView.swapAdapter(adapter, false);
    adapter.notifyDataSetChanged();
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (isVisibleToUser) {
      refreshInventoryFragment();
    }
  }
}

