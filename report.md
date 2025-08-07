 - Ideální by bylo mít http client pro každé repository, zde ovšem není, jelikož každá repository podporuje pouze jeden typ requestu.
 - Pro vztvoření modelů resp. DTOs by bylo dobré použít nějaký generátor, který by vygeneroval DTOs z OpenAPI specifikace, zde nebylo využito, z důvodu časové náročnosti konfigurace a malého množství DTOs.
 - Jelikož je corellation-id v hlavičce pouze jako optional a stejně tak i v dalších použitých API a není nějak zřejmé jak se k němu chovat, je pouze přeposíláno a v případě, že chybí, není to jakkoliv řešeno.
 - Pro vytváření logů pro účely budoucího auditu jsou využity interceptory, jelikož je vyžadováno logování pouze dotazů/odpovědí a volání na externí API. V případě, že by bylo chtěné logovat především jiné události, bylo by možná vhodné využít robustnější service či externího nástroje. Pro drobné logy je ovšem možné využít připravenou service LoggingService.
 - Dle API specifikace api vrací pouze jediný chybový stav 400, aplikace je ale připravena rozlišovat i další chybové stavy, které by mohly být v budoucnu přidány.
 - Jelikož servisní vrstva obsauhuje minimum business logiky, nejsou jednotlivé metody testovány, ale pouze celá vrstva jako celek.
 - Na posledí chvíli jsem si uvědomil, požadavky:
    - Aplikace má pokryté logování pro případné dohledávky za provozu
    - Jelikož se jedná o auditovaný systém, musíme v aplikaci mít auditní log pro
      budoucí případné dohledávky request, response a případné API volání BEs.
      Tento auditní log bude také uložen v DB.
    - Požadavek na logování je pokryt pouze v rámci auditních logů, jelikož jsem sám sebe přesvědčil, že to se po mě chce, v aplikaci toho rozměru je to nejspíše i dostatečné, jelikož obsahuje minimum business logiky, ale nepokrývá například selhání DB apod.
 - Data pro testovací scénáře byly částečně generovány s pomocí GitHub Copilot, částečně ručně.