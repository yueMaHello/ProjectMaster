/*
 * Copyright (C) 2016 CMPUT301F16T18 - Alan(Xutong) Zhao, Michael(Zichun) Lin, Stephen Larsen, Yu Zhu, Zhenzhe Xu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mayingnan.project301.utilities;

import com.example.mayingnan.project301.User;
import com.google.gson.Gson;

/**
 * Utility class that help to serilize
 * and deserialize the user object
 */
public class UserUtil {
    public static String serializer(User user) {
        Gson gson = new Gson();
        return gson.toJson(user);
    }

    public static User deserializer(String string) {
        Gson gson = new Gson();
        return gson.fromJson(string, User.class);
    }
}