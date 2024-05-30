package com.example.assistantme;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;

import androidx.core.content.ContextCompat;

import com.siddharthks.bubbles.FloatingBubbleConfig;
import com.siddharthks.bubbles.FloatingBubbleService;

public class floating_chat_head extends FloatingBubbleService {

    @Override
    protected FloatingBubbleConfig getConfig() {
        Context context=getApplicationContext();
        return new FloatingBubbleConfig.Builder()
                // Set the drawable for the bubble
                .bubbleIcon(ContextCompat.getDrawable(context,R.drawable.chat_head_logo))

                // Set the drawable for the remove bubble
                .removeBubbleIcon(ContextCompat.getDrawable(context,R.drawable.exit_chat_head))

                // Set the size of the bubble in dp
                .bubbleIconDp(54)

                // Set the size of the remove bubble in dp
                .removeBubbleIconDp(54)

                // Set the padding of the view from the boundary
                .paddingDp(4)

                // Set the radius of the border of the expandable view
                .borderRadiusDp(0)

                // Does the bubble attract towards the walls
                .physicsEnabled(true)

                // The color of background of the layout
                .expandableColor(Color.WHITE)

                // The color of the triangular layout
                .triangleColor(Color.WHITE)

                // Horizontal gravity of the bubble when expanded
                .gravity(Gravity.LEFT)

                // The view which is visible in the expanded view
                .expandableView(getInflater().inflate(R.layout.activity_main,null))

                .removeBubbleAlpha(0.75f)


                // Building
                .build();
    }
}

