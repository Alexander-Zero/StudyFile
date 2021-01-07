package com.alex.zero.jvm.code;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2020/12/17
 */
public class Test {
    private int age;
    private String name;

    private void setAge(int age) {
        this.age = age;
    }

    private void setName(String name) {
        this.name = name;
    }

    private int addAge(int addYear) {
        return this.age + addYear;
    }

    private int minusAge(int rawYear, int minusYear) {
        int year = rawYear - minusYear;
        year += 20;
        return year;
    }

    public static void main(String[] args) {

        Test t = new Test();
        t.setName("张山");
        t.setAge(18);

        int addYear = t.addAge(20);
        int minusAge = t.minusAge(addYear, 16);

        System.out.println(t.name + " age is " + minusAge);


    }

}
