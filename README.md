# FlexboxRadiogroup
 A radio group that automatically adjust the children to go to the next line if necessary while keeping all the functionalities of RadioGroup
 
 
A radio group that extends from [FlexboxLayout](https://github.com/google/flexbox-layout), allowing it to automatically adjust the children to go to the next line if necessary. Has all the functionality of RadioGroup

Children are to be of AppCompatRadioButton

In order to add spacing between items and between vertical lines, set the horizontal/vertical dividers to true (showDividerVertical and showDividerHorizontal) and set the divider drawable (dividerDrawableVertical and dividerDrawableHorizontal).


# Use
Add the following line to your project's `build.gradle`:
```
maven { url 'https://jitpack.io' }
```

Add the following lines to your app's `build.gradle`:
```
implementation 'com.github.khash021:flexbox-radiogroup:1.0.1'
```


# License
Copyright 2021 Khash021
