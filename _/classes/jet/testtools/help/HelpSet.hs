<?xml version='1.0' encoding='ISO-8859-1' ?>

<helpset version="1.0">

  <title>Example of Help</title>

  <maps>
     <homeID>main</homeID>
     <mapref location="HelpMap.jhm"/>
  </maps>

  <view>
    <name>Index</name>
    <label>Index</label>
    <type>javax.help.IndexView</type>
    <data>HelpIndex.xml</data>
  </view>

  <view>
    <name>Search</name>
    <label>Search</label>
    <type>javax.help.SearchView</type>
    <data engine="com.sun.java.help.search.DefaultSearchEngine">
      MasterSearchIndex
    </data>
  </view>

</helpset>
