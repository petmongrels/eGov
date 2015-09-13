
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'EGF','true','egf',null,'Financials',4);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Assigned Revenue Reports','false',null,(select id from eg_module where name='EGF'),'Assigned Revenue Reports',9);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'TNEB','false',null,(select id from eg_module where name='EGF'),'TNEB',10);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'EGF-COMMON','false','egf',(select id from eg_module where name='EGF'),'EGF-COMMON',null);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Budgeting','true',null,(select id from eg_module where name='EGF'),'Budgeting',8);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Loans and Grants','false',null,(select id from eg_module where name='EGF'),'Loans and Grants',9);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Transactions','true',null,(select id from eg_module where name='EGF'),'Transactions',1);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Reports','true',null,(select id from eg_module where name='EGF'),'Reports',2);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Masters','true',null,(select id from eg_module where name='EGF'),'Masters',3);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Period End Activities','true',null,(select id from eg_module where name='EGF'),'Period End Activities',4);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Set-up','true',null,(select id from eg_module where name='EGF'),'Set-up',5);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Deductions','true',null,(select id from eg_module where name='EGF'),'Deductions',7);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Chart Of Account','true',null,(select id from eg_module where name='Budgeting'),'Chart Of Account',2);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Budget Addition Appropriation','true',null,(select id from eg_module where name='Budgeting'),'Budget Addition Appropriation',1);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Budget Reports','true',null,(select id from eg_module where name='Reports'),'Budget Reports',4);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Budget Details','true',null,(select id from eg_module where name='Budgeting'),'Budget Details',3);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Budget Definition','true',null,(select id from eg_module where name='Budgeting'),'Budget Definition',1);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Remittance Reports','true',null,(select id from eg_module where name='Reports'),'Remittance Reports',5);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Loans Reports','true',null,(select id from eg_module where name='Loans and Grants'),'Reports',1);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Fund','true',null,(select id from eg_module where name='Masters'),'Fund',2);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'User Defined Codes','true',null,(select id from eg_module where name='Masters'),'User Defined Codes',2);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Function','true',null,(select id from eg_module where name='Masters'),'Function',3);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Budget Report','true',null,(select id from eg_module where name='Budgeting'),'Budget Report',4);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Receipts','false',null,(select id from eg_module where name='Transactions'),'Receipts',1);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Expenditures','true',null,(select id from eg_module where name='Transactions'),'Expenditures',2);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Journal Vouchers','true',null,(select id from eg_module where name='Transactions'),'Journal Vouchers',3);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Contra Entries','true',null,(select id from eg_module where name='Transactions'),'Contra Entries',4);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'BRS','true',null,(select id from eg_module where name='Transactions'),'BRS',5);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Financial Statements','true',null,(select id from eg_module where name='Reports'),'Financial Statements',1);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Accounting Records','true',null,(select id from eg_module where name='Reports'),'Accounting Records',2);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'MIS Reports','true',null,(select id from eg_module where name='Reports'),'MIS Reports',3);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Chart of Accounts','true',null,(select id from eg_module where name='Masters'),'Chart of Accounts',1);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Supplier/Contractors','false',null,(select id from eg_module where name='Masters'),'Supplier/Contractors',9);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Schemes','true',null,(select id from eg_module where name='Masters'),'Schemes and Sub Schemes',10);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Salary Codes','false',null,(select id from eg_module where name='Set-up'),'Salary Codes',5);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Report Schedule Mapping','true',null,(select id from eg_module where name='Set-up'),'Report Schedule Mapping',4);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Cheque Printing Format','true',null,(select id from eg_module where name='Set-up'),'Cheque Printing Format',5);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Remittance Recovery','true',null,(select id from eg_module where name='Deductions'),'Remittance Recovery',2);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Party Types','true',null,(select id from eg_module where name='Masters'),'Party Types',6);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Contract Types','true',null,(select id from eg_module where name='Masters'),'Contract Types',7);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Recovery Masters','true',null,(select id from eg_module where name='Set-up'),'Recovery Masters',8);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'TNEB Transactions','true',null,(select id from eg_module where name='TNEB'),'Transactions',2);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'TNEB Payment Processing','true',null,(select id from eg_module where name='TNEB'),'Payment Processing',3);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'TNEB Reports','true',null,(select id from eg_module where name='TNEB'),'Reports',null);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'TNEB Masters','true',null,(select id from eg_module where name='TNEB'),'Master',1);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Procurement Orders','false',null,(select id from eg_module where name='Expenditures'),'Procurement Orders',1);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Bill Registers','true',null,(select id from eg_module where name='Expenditures'),'Bill Registers',2);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Bills Accounting','true',null,(select id from eg_module where name='Expenditures'),'Bills Accounting',3);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Payments','true',null,(select id from eg_module where name='Expenditures'),'Payments',4);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'TNEB Account','true',null,(select id from eg_module where name='TNEB Masters'),'TNEB Account',3);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Target Area','true',null,(select id from eg_module where name='TNEB Masters'),'Target Area',4);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'TNEB Bill Processing','true',null,(select id from eg_module where name='TNEB Transactions'),'Bill Processing',5);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Salary Processing','true',null,(select id from eg_module where name='Payments'),'Salary Processing',2);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Pension Processing','true',null,(select id from eg_module where name='Payments'),'Pension Processing',3);
Insert into eg_module ("id","name","enabled","contextroot","parentmodule","displayname","ordernumber") values (nextval('SEQ_EG_MODULE'),'Salary Bills','false',null,(select id from eg_module where name='Bill Registers'),'Salary Bills',6);