package com.example.dictionary.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static jakarta.persistence.TemporalType.TIMESTAMP;

@Entity
@Table(name = "words")
public class Word {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(insertable = false, updatable = false)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToMany(cascade = PERSIST, fetch = LAZY)
    @JoinTable(name = "word_definition",
            joinColumns = @JoinColumn(name = "word_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "definition_id", referencedColumnName = "id"))
    private final Set<Definition> definitions = new HashSet<>();

    @ManyToMany(cascade = PERSIST, fetch = LAZY)
    @JoinTable(name = "word_example",
            joinColumns = @JoinColumn(name = "word_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "example_id", referencedColumnName = "id"))
    private final Set<Example> examples = new HashSet<>();

    @ManyToMany(fetch = LAZY)
    @JoinTable(name = "word_synonyms",
            joinColumns = @JoinColumn(name = "word_id"),
            inverseJoinColumns = @JoinColumn(name = "synonym_id"))
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    private final Set<Word> synonyms = new HashSet<>();

    @ManyToMany(fetch = LAZY)
    @JoinTable(name = "word_antonyms",
            joinColumns = @JoinColumn(name = "word_id"),
            inverseJoinColumns = @JoinColumn(name = "antonym_id"))
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    private final Set<Word> antonyms = new HashSet<>();

    @ManyToOne(fetch = EAGER, cascade = {PERSIST, MERGE})
    @JoinColumn(nullable = false)
    private Category category;

    @ManyToMany(fetch = LAZY)
    @JoinTable(name = "word_contributors",
            joinColumns = @JoinColumn(name = "word_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private final Set<User> contributors = new HashSet<>();

    @OneToMany(mappedBy = "word", orphanRemoval = true, fetch = LAZY)
    private final List<Comment> comments = new ArrayList<>();

    @Temporal(value = TIMESTAMP)
    private LocalDateTime addedAt;

    @ManyToMany(fetch = LAZY)
    @JoinTable(
            name = "word_dictionary",
            joinColumns = @JoinColumn(name = "word_id"),
            inverseJoinColumns = @JoinColumn(name = "dictionary_id")
    )
    @Cascade(value = CascadeType.PERSIST)
    private final Set<Dictionary> dictionaries = new HashSet<>();

    public Word() {
    }

    public Word(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Definition> getDefinitions() {
        return definitions;
    }

    public Set<Example> getExamples() {
        return examples;
    }

    public Set<Word> getSynonyms() {
        return synonyms;
    }

    public Set<Word> getAntonyms() {
        return antonyms;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<User> getContributors() {
        return contributors;
    }

    public void addContributor(User user) {
        contributors.add(user);
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public void addDefinition(Definition definition) {
        definitions.add(definition);
        definition.addWord(this);
        dictionaries.addAll(definition.getDictionaries());
    }

    public void removeDefinition(Definition definition) {
        getDefinitions().remove(definition);
        definition.getWords().remove(this);
    }

    public void addSynonym(Word synonym) {
        if (!synonyms.contains(synonym)) {
            synonyms.add(synonym);
            synonym.addSynonym(this);
            dictionaries.addAll(synonym.getDictionaries());
        }
    }

    public void removeSynonym(Word synonym) {
        if (getSynonyms().contains(synonym)) {
            getSynonyms().remove(synonym);
            synonym.removeSynonym(this);
        }
    }

    public void addAntonym(Word antonym) {
        if (!antonyms.contains(antonym)) {
            antonyms.add(antonym);
            antonym.addAntonym(this);
            dictionaries.addAll(antonym.getDictionaries());
        }
    }

    public void removeAntonym(Word antonym) {
        if (getAntonyms().contains(antonym)) {
            getAntonyms().remove(antonym);
            antonym.removeAntonym(this);
        }
    }

    public void addExample(Example example) {
        examples.add(example);
        example.addWord(this);
    }

    public void removeExample(Example example) {
        getExamples().remove(example);
        example.getWords().remove(this);
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setWord(this);
    }

    public void removeComment(Comment comment) {
        getComments().remove(comment);
        comment.setWord(null);
    }

    public Set<Dictionary> getDictionaries() {
        return dictionaries;
    }

    public void addDictionary(Dictionary dictionary){
        dictionaries.add(dictionary);
        dictionary.addWord(this);
    }

    public void removeDictionary(Dictionary dictionary) {
        getDictionaries().remove(dictionary);
        dictionary.getWords().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return Objects.equals(name, word.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
