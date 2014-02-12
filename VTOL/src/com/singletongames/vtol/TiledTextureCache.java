package com.singletongames.vtol;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.debug.Debug;

import android.content.Context;
import android.graphics.BitmapFactory;

public class TiledTextureCache {
private static Map<String, ITextureRegion> textures = new HashMap<String, ITextureRegion>();
private static Context ctx;
private static TextureManager tm;

public static void clear() {
    textures.clear();
}


public static void init(Context pCtx, TextureManager pTm) {
    ctx = pCtx;
    tm = pTm;
}


public static ITextureRegion getTexture(String name) {
    ITextureRegion texture = textures.get(name);
    if (texture == null) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        try {
            InputStream in = ctx.getResources().getAssets().open("gfx/" + name);
            BitmapFactory.decodeStream(in, null, opt);
        } catch (IOException e) {
            Debug.e("TextureCache", "Could not load texture [" + name + "]", e);
        }
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        BitmapTextureAtlas texAtlas = new BitmapTextureAtlas(tm, opt.outWidth, opt.outHeight, TextureOptions.DEFAULT);
        texture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texAtlas, ctx, name, 0, 0);
        texAtlas.load();    
        textures.put(name, texture);
    }
    return texture;
}
}
