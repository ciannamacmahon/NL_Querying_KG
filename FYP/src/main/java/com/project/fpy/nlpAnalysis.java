package com.project.fpy;

//nlpPipeline.java
import java.util.LinkedList;
import java.util.Properties;
import java.util.stream.Collectors;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.semgraph.*;
import edu.stanford.nlp.util.CoreMap;

public class nlpAnalysis {
    public static LinkedList<String >qualities=new LinkedList<>();

    static StanfordCoreNLP pipeline;
    public static void main(String[]args){
        String question="When was Obama born?";
        nlpAnalysis.init();
        nlpAnalysis.findPOStags(question);
        qualities=nlpAnalysis.dependancyParser(question);
        nlpAnalysis.pringQual(qualities);
    }

    //Test method; each item in the list is the characteristic of that word
    public static void pringQual(LinkedList <String>qualOfQuery){
        for (String line:qualOfQuery){
            System.out.println(line);

        }
    }
    public static void init()
    {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse");
        pipeline = new StanfordCoreNLP(props);
    }

    public static LinkedList<String> dependancyParser(String q1)
    {
        String textQualitites="";
        LinkedList depParse= new LinkedList();
        Annotation document = new Annotation(q1);
        pipeline.annotate(document);

        // for NER but i think this can all be done with same object type- look into this more
        CoreDocument doc=new CoreDocument(q1);
        pipeline.annotate(doc);
        //Assumes there is only one sentence in the document ie. the question
        CoreMap sentence =document.get(CoreAnnotations.SentencesAnnotation.class).get(0);

        NER(doc);
        //Get constituency parse: CFG type output- nounPhrasees etc
        Tree constituencyParse = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
        System.out.println("Constituency Parse:");
        System.out.println(constituencyParse);

        //Gets the dependancy Parse
        SemanticGraph dependencyParse =
                    sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
        System.out.println("Depenecy parse");
        for (TypedDependency typedDependency : dependencyParse.typedDependencies()) {
            String line = typedDependency.toString();
            depParse.add(line);
        }

        return depParse;
    }

    public static void NER(CoreDocument doc){
        System.out.println("---");
        System.out.println("entities found");
        for (CoreEntityMention em : doc.entityMentions())
            System.out.println("\tdetected entity: \t"+em.text()+"\t"+em.entityType());
        System.out.println("---");
        System.out.println("tokens and ner tags");
        String tokensAndNERTags = doc.tokens().stream().map(token -> "("+token.word()+","+token.ner()+")").collect(
                Collectors.joining(" "));
        System.out.println(tokensAndNERTags);

    }
    public static void findPOStags(String query){
        CoreDocument doc=pipeline.processToCoreDocument(query);
        //display the tagged text
        for(CoreLabel token : doc.tokens()){
            System.out.println(String.format("%s\t%s", token.word(),token.tag()));
        }

  }


}