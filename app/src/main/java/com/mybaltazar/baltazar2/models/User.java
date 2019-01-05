package com.mybaltazar.baltazar2.models;

import java.io.Serializable;

public class User implements Serializable
{
    public int id, status;
    public String email, phone, role, name, avatar_url;
    public StudentMeta student_meta;
    public String avatar;
    public int activation_code;
}
