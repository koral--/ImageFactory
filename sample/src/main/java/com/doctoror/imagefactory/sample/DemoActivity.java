/*
 * Copyright (C) 2015 Yaroslav Mytkalyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.doctoror.imagefactory.sample;

import com.doctoror.imagefactory.ImageFactory;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class DemoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        final GridView grid = (GridView) findViewById(R.id.activity_demo_grid);
        grid.setAdapter(new DemoAdapter(this, generateImageInfo()));
    }

    @NonNull
    private List<Drawable> generateImageInfo() {
        final String[] names = new String[]{
                "Rotating_earth_(large).gif",
                "Rotating_earth_(large).gif",
                "Rotating_earth_(large).gif",
                "Rotating_earth_(large).gif",
                "Rotating_earth_(large).gif",
                "Rotating_earth_(large).gif",
                "Rotating_earth_(large).gif",
                "Rotating_earth_(large).gif",
                "Rotating_earth_(large).gif",
                "Rotating_earth_(large).gif",
                "Rotating_earth_(large).gif",
                "Rotating_earth_(large).gif",
                "Rotating_earth_(large).gif",
                "Rotating_earth_(large).gif",
                "Rotating_earth_(large).gif",
                "Rotating_earth_(large).gif",

//                "Static_earth.gif",
//                "LoopOnce.gif",
//                "small.gif",
//                "smallest.gif",
//                "ru9gag.gif",
//                "ru9gag1.gif",
//                "ru9gag3.gif",
//                "ru9gag4.gif"
        };
        final List<Drawable> list = new ArrayList<>(names.length);
        for (final String name : names) {
            InputStream is = null;
            try {
                is = getAssets().open(name, AssetManager.ACCESS_RANDOM);
                final Drawable item = ImageFactory.decodeStream(getResources(), is);
                if (item != null) {
                    list.add(item);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return list;
    }

    private static final class DemoAdapter extends BaseAdapter2<Drawable> {

        private DemoAdapter(@NonNull final Context context,
                @Nullable final List<Drawable> items) {
            super(context, items);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final ImageView imageView;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.grid_item_demo, parent, false);
                imageView = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(imageView);
            } else {
                imageView = (ImageView) convertView.getTag();
            }

            final Drawable item = getItem(position);
            Drawable.Callback callback = item.getCallback();
            imageView.setImageDrawable(item);
            if (!(callback instanceof MultiCallback)) {
                callback = new MultiCallback();
            }
            ((MultiCallback) callback).addView(imageView);
            item.setCallback(callback);

            return convertView;
        }
    }

    public static class MultiCallback implements Drawable.Callback {
        private Set<WeakReference<View>> mViewSet = new LinkedHashSet<>();

        @Override
        public void invalidateDrawable(Drawable who) {
            Iterator<WeakReference<View>> iterator = mViewSet.iterator();
            while (iterator.hasNext()) {
                final View view = iterator.next().get();
                if (view != null) {
                    view.invalidateDrawable(who);
                }
            }
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            Iterator<WeakReference<View>> iterator = mViewSet.iterator();
            while (iterator.hasNext()) {
                final View view = iterator.next().get();
                if (view != null) {
                    view.scheduleDrawable(who, what, when);
                }
            }
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            Iterator<WeakReference<View>> iterator = mViewSet.iterator();
            while (iterator.hasNext()) {
                final View view = iterator.next().get();
                if (view != null) {
                    view.unscheduleDrawable(who, what);
                }
            }
        }

        public void addView(View view) {
            mViewSet.add(new WeakReference<>(view));
        }

        public void removeView(View view) {
            Iterator<WeakReference<View>> iterator = mViewSet.iterator();
            while (iterator.hasNext()) {
                if (view ==iterator.next().get()) {
                    iterator.remove();
                }
            }
        }
    }
}
