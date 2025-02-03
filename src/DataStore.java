import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataStore {
    
    public enum DataType{
        STRING, LIST, SET, HASH
    }
    private interface Collection{};
    private static class ListCollection implements Collection{
        private final List<String> list = new CopyOnWriteArrayList<>();
    }
    private static class SetCollection implements Collection{
        private final Set<String> set = ConcurrentHashMap.newKeySet();
    }
    private static class HashCollection implements Collection{
        private final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
    }


    //map for storing Strings
    public final ConcurrentHashMap<String, String> stringStore = new ConcurrentHashMap<>();
    //map that maps the name to the data type
    private final ConcurrentHashMap<String ,DataType> typeMap = new ConcurrentHashMap<>();
    //map that stores all collection
    private final ConcurrentHashMap<String, Collection> collectionStore = new ConcurrentHashMap<>();

    //Methods for list
    public void createList(String key){
        if(key==null||typeMap.containsKey(key)) return;
        collectionStore.put(key, new ListCollection());
        typeMap.put(key, DataType.LIST);
    }
    public void lPush(String key, String value){
        if(!typeMap.contains(key)){
            createList(key);
        }else if(!typeMap.get(key).equals(DataType.LIST)){
            throw new IllegalArgumentException("Key is not a List");
        }
        ListCollection list = (ListCollection)collectionStore.get(key);
        list.list.add(0, value);
    }
    public String lPop(String key){
        if(!typeMap.getOrDefault(key, DataType.STRING).equals(DataType.LIST)) throw  new IllegalArgumentException("Key is not a List");
        ListCollection list = (ListCollection) collectionStore.get(key);
        if(list.list.size()>=1){
            return list.list.removeFirst();
        }else{
            throw new IllegalArgumentException("Lisr is not long enough");
        }

    }
    public int lSize(String key){
        if(!typeMap.getOrDefault(key, DataType.STRING).equals(DataType.LIST)) throw  new IllegalArgumentException("Key is not a List");
        ListCollection list = (ListCollection) collectionStore.get(key);
        return list.list.size();
    } 

    //methods for hash
    public void createHash(String key){
        if(key==null||typeMap.containsKey(key)) return;
        collectionStore.put(key, new HashCollection());
        typeMap.put(key, DataType.HASH);
    }
    //takes in map name, key name, and value name
    public void hSet(String key, String field, String value){
        if(!typeMap.containsKey(key)){
            createHash(key);
        }else if(!typeMap.get(key).equals(DataType.HASH)){
            throw new IllegalArgumentException("Key is not a Hash");
        }
        HashCollection hash = (HashCollection)collectionStore.get(key);
        hash.map.put(field, value);
    }
    public String hGet(String key, String field){
        if(!typeMap.getOrDefault(key, DataType.STRING).equals(DataType.HASH)) throw new IllegalArgumentException("Key is not a Hash");
        HashCollection hashCollection = (HashCollection)collectionStore.get(key);
        if(hashCollection.map.containsKey(field)){
            return hashCollection.map.get(field);
        }else{
            throw new IllegalArgumentException("Key DNE");
        }
        
    }
    public void hDelete(String key, String field){
        if(!typeMap.getOrDefault(key, DataType.STRING).equals(DataType.HASH)) throw new IllegalArgumentException("Key is not a Hash");
        HashCollection hashCollection = (HashCollection)collectionStore.get(key);
        hashCollection.map.remove(field);
    }
    public boolean hExists(String key, String field){
        if(!typeMap.getOrDefault(key, DataType.STRING).equals(DataType.HASH)) throw new IllegalArgumentException("Key is not a Hash");
        HashCollection hashCollection = (HashCollection)collectionStore.get(key);
        return hashCollection.map.containsKey(field);
    }
    //methods for set
    public void createSet(String key){
        if(key==null||typeMap.containsKey(key)) return;
        collectionStore.put(key, new SetCollection());
        typeMap.put(key, DataType.SET);
    }
    public void sAdd(String key, String value){
        // If key doesn't exist in typeMap, create new hash
        if (!typeMap.containsKey(key)) {
            createSet(key);
        } else if (!typeMap.get(key).equals(DataType.SET)) {
            // If key exists but isn't a hash, that's an error
            throw new IllegalArgumentException("Key exists but is not a SET");
        }
        
        SetCollection set = (SetCollection) collectionStore.get(key);
        set.set.add(value);
    }

    public void sRemove(String key, String value){
        if(!typeMap.getOrDefault(key, DataType.STRING).equals(DataType.SET)) throw  new IllegalArgumentException("Key is not a List");
        SetCollection set = (SetCollection) collectionStore.get(key);
        set.set.remove(value);
    }
    
    public boolean sContains(String key, String value){
        if(!typeMap.getOrDefault(key, DataType.STRING).equals(DataType.SET)) throw  new IllegalArgumentException("Key is not a List");
        SetCollection set = (SetCollection) collectionStore.get(key);
        return set.set.contains(value);
    }
    public int sSize(String key){
        if(!typeMap.getOrDefault(key, DataType.STRING).equals(DataType.SET)) throw  new IllegalArgumentException("Key is not a List");
        SetCollection set = (SetCollection) collectionStore.get(key);
        return set.set.size();
    }
}
