package com.rudie.severin.textadventure.FragmentClasses;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.rudie.severin.textadventure.InformationHolders.CurrentInventoryAndStats;
import com.rudie.severin.textadventure.InformationHolders.ImageConstructor;
import com.rudie.severin.textadventure.InformationHolders.ItemData;
import com.rudie.severin.textadventure.R;
import com.rudie.severin.textadventure.DatabaseClasses.DBInterfacer;
import com.rudie.severin.textadventure.InformationHolders.PH;

public class PopupFragment extends DialogFragment {
    PopupCompleteListener mCallback;
    Context mContext;

    public static PopupFragment newInstance() {
        return new PopupFragment();
    }

    public interface PopupCompleteListener {
        public void closePopupNow();
    }

    TextView tvText, tvDamage, tvItem;
    ImageView image;
    Button button;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popup, container, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview_image_popupFragment);
        button = (Button) view.findViewById(R.id.button_continue_popupFragment);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDestroyView();
            }
        });

        Bundle bundle = getArguments();
        int charId = bundle.getInt(PH.tbl_character_id);
        int popupId = bundle.getInt(PH.tbl_popup_id);
        DBInterfacer helper = DBInterfacer.getInstance(mContext);
        Bundle popupData = helper.getPopupData(popupId);
        int damage = popupData.getInt(PH.tbl_popup_damage);
        int item = popupData.getInt(PH.tbl_popup_item);

        if (item == PH.HAT_FLAG) {
            popupId = PH.HAT_POPUP;
            // TODO: add a hat here
        } else if (item == PH.MONTAGE_STRENGTH_FLAG) {
//            popupId = PH.MONTAGE_STRENGTH_POPUP;
            helper.upgradeCharacterStat(charId, PH.STRENGTH_ID);
        } else if (item == PH.MONTAGE_AGILITY_FLAG) {
//            popupId = PH.MONTAGE_AGILITY_POPUP;
            helper.upgradeCharacterStat(charId, PH.AGILITY_ID);
        } else if (item == PH.MONTAGE_COMRADERY_FLAG) {
//            popupId = PH.MONTAGE_COMRADERY_POPUP;
            helper.upgradeCharacterStat(charId, PH.COMRADERY_ID);
        } else if (item == PH.MECHASPIDER_DEAD) {
            popupId = PH.MECHASPIDER_POPUP;
        }

        popupData = helper.getPopupData(popupId);
        String text = popupData.getString(PH.tbl_popup_text);
        text = insertNamesIntoPopupText(text, charId);

        tvText = (TextView) view.findViewById(R.id.textview_text_popupFragment);
        tvText.setText(text);

        tvDamage = (TextView) view.findViewById(R.id.textview_damageTaken_popupFragment);
        if (damage != -1) {
            tvDamage.setText("You took " + damage + " damage!");
            tvDamage.setVisibility(View.VISIBLE);
            helper.setCharacterDamageDealt(damage, charId);
        }
        else {
            tvDamage.setVisibility(View.GONE);
        }

        tvItem = (TextView) view.findViewById(R.id.textview_itemFound_popupFragment);
        if (item == 1) {
            tvItem.setVisibility(View.VISIBLE);
            ItemData template = CurrentInventoryAndStats.getRandomItem();
            ItemData newItem = new ItemData(template.getItemName(), template.getItemPower(),
                    template.getItemTypeId(), template.getItemStatId(), charId, template.getAcquireText());
            template = null;
            helper.addItemToInventory(newItem);
            CurrentInventoryAndStats.refreshFromDb(charId, getContext());
//            InventoryFragment.refreshInventoryFragment
            tvItem.setText(newItem.getItemName());
            tvText.setText(newItem.getAcquireText());
//            System.out.println("");
        } else if (item == 6) {
            tvItem.setVisibility(View.VISIBLE);
            ItemData spiderFlag = new ItemData("Extendo-Flag", 1, PH.ITEM_TYPE_FLAG, PH.COMRADERY_ID, charId, "Mechaspiders are not built in bulk, they require intensely strong power sources to properly animate.  Making your way over to it, you pry away the plating from its core and seize the core they had been using.  Hefting it above your head you admire the Fusion Powered Extendable Patriotism Unit.  You''d thought these were still in prototype testing.  As you admire the device a &BadMinion& patrol car rounds the corner, firing off its Inferiorlaser.  You duck back as a beam sears into your side.  Now is not the time for reverie, there''s a planet to free.");
            helper.addItemToInventory(spiderFlag);
            CurrentInventoryAndStats.refreshFromDb(charId, getContext());
            tvItem.setText(spiderFlag.getItemName());
            tvText.setText(spiderFlag.getAcquireText());
        } else {
            tvItem.setVisibility(View.GONE);
        }

        String image = popupData.getString(PH.tbl_popup_image);
        if (image.equals(PH.NULL)) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
//            Drawable imageDrawable = getResources().getDrawable(ImageConstructor.getInstance().getDrawable(image));
//            imageView.setImageDrawable(imageDrawable);
            // TODO: add ion here once it's working
            String url = ImageConstructor.getInstance().getDrawable(image);
            Ion.with(imageView)
                    .placeholder(R.color.colorPrimary)
                    .error(R.color.colorAccent)
//                .animateLoad(spinAnimation)
//                .animateIn(fadeInAnimation)
                    .load(url)
                    .withBitmapInfo();
//                    .setCallback(NotFoundImageLoader.handleNotFound(holder.photo, mContext));
        }

        tvText.setText(cleanFormattingFromText(tvText.getText().toString()));

        return view;
    }

    private String cleanFormattingFromText(String text) {
        text = text.replace("`", "'");
        return text;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            mCallback = (PopupCompleteListener) mContext;
            mCallback.closePopupNow();
        } catch (ClassCastException e) {
            e.printStackTrace();
            throw new ClassCastException(mContext.toString()
                    + " must implement PopupCompleteListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private String insertNamesIntoPopupText(String popupText, int charId) {
        DBInterfacer helper = DBInterfacer.getInstance(getActivity());
        String[] firstLastNick = helper.getCharacterFirstNickLast(charId, getActivity());
        popupText = popupText.replace("FIRSTNAME", firstLastNick[0]);
        popupText = popupText.replace("NICKNAME", firstLastNick[1]);
        popupText = popupText.replace("LASTNAME", firstLastNick[2]);
        popupText = popupText.replace("&PlayerCharacter&", firstLastNick[0] +" " + firstLastNick[1]
                + " " + firstLastNick[2]);
        return popupText;
    }




}

