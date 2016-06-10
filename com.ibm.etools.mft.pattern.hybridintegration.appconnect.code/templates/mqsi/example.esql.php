BROKER SCHEMA mqsi
<?php 
	if ($_MB['PP']['errorAction'] == 'errorQueue') {
		echo "DECLARE ErrorAction EXTERNAL CHARACTER '".$_MB['PP']['errorAction']."';";

echo <<<ESQL

CREATE FILTER MODULE CheckErrorAction
    CREATE FUNCTION Main() RETURNS BOOLEAN
    BEGIN
        IF ErrorAction = 'errorQueue' THEN 
            RETURN TRUE; 
        ELSE
			RETURN FALSE;
        END IF;
    END;
END MODULE;

ESQL;
	}
?>
