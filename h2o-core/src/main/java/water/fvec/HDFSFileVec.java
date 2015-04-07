package water.fvec;

import water.DKV;
import water.Futures;
import water.Key;
import water.Value;

/**
 * Vec representation of file stored on HDFS.
 */
public class HDFSFileVec extends FileVec {
  private HDFSFileVec(Key key, long len) {
    super(key, len, Value.HDFS);
  }

  public static Key make(String path, long size) {
    Futures fs = new Futures();
    Key key = make(path, size, fs);
    fs.blockForPending();
    return key;
  }
  public static Key make(String path, long size, Futures fs) {
    Key k = Key.make(path);
    Key k2 = Vec.newKey(k);
    new Frame(k).delete_and_lock(null);
    // Insert the top-level FileVec key into the store
    Vec v = new HDFSFileVec(k2,size);
    DKV.put(k2, v, fs);
    Frame fr = new Frame(k,new String[]{path},new Vec[]{v});
    fr.update(null);
    fr.unlock(null);
    return k;
  }

  @Override public int setChunkSize(Frame fr, int chunkSize) {
    clearAllCachedChunks();
    return super.setChunkSize(fr, chunkSize);
  }
}
