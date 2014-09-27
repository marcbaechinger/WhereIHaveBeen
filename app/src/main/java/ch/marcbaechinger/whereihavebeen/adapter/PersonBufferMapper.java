package ch.marcbaechinger.whereihavebeen.adapter;

import com.google.android.gms.plus.model.people.Person;

import ch.marcbaechinger.whereihavebeen.model.PersonData;

public class PersonBufferMapper {

    public PersonData map(Person person) {
        PersonData personData = new PersonData();
        personData.setDisplayName(person.getDisplayName());
        personData.setImageUrl(person.getImage().getUrl());
        return personData;
    }
}
