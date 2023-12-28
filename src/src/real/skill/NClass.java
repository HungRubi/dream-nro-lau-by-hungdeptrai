package real.skill;

import server.Util;

public class NClass {

    public int classId;

    public String name;

    public SkillTemplate[] skillTemplates;
    
    public SkillTemplate getSkillTemplate(int tempId){
        for (SkillTemplate skillTemplate : skillTemplates) {
            if (skillTemplate.id == tempId){
                return skillTemplate;
            }
        }
        return null;
    }
    
    public SkillTemplate getSkillTemplateByName(String name){
        for (SkillTemplate skillTemplate : skillTemplates) {
            if((Util.removeAccent(skillTemplate.name).toUpperCase()).contains((Util.removeAccent(name)).toUpperCase())){
                return skillTemplate;
           }
        }
        return null;
    }
    
}
